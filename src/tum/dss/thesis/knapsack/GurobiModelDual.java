package tum.dss.thesis.knapsack;

import gurobi.GRB;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math3.util.FastMath;

import tum.dss.thesis.LinearProgramInterface;
import tum.dss.thesis.MechanismDataInterface;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Gurobi model to solve relaxed IP (KNP).
 * @author Florian Stallmann
 *
 */
public class GurobiModelDual implements LinearProgramInterface {
	private static final Logger log = Logger.getLogger( GurobiModelDual.class.getName() );
	
	/** Mechanism data to run gurobi on */
	protected MechanismDataInterface data;
	
	/** Save index of solutions where x<sub>i,j</sub> &gt; 0 */
	protected ListMultimap<Integer, Integer> sol_used = LinkedListMultimap.create();
	
	/** Save value of solution where x<sub>i,j</sub> &gt; 0 */
	protected Map<String, Double> sol_values = new HashMap<String, Double>();
	
	/** Objective value */
	protected double     obj;
	
	/** Will save string representation of object to improve performance */
	private String to_string = "";
	
	/** Gurobi environment */
	private GRBEnv     env ;
	/** Gurobi model */
	private GRBModel   model;
	
	/**
	 * Initializes without data.
	 */
	public GurobiModelDual() {
	}
	
	/**
	 * Initializes with data.
	 * @param data mechanism data.
	 */
	public GurobiModelDual(final MechanismDataInterface data) {
		this();
		setData(data);
	}
	
	@Override
	public void setData(final MechanismDataInterface data) {
		this.data  = data;
		sol_used.clear();
		to_string = "";
//		y_double = new double[data.getNumPlayers()+1];
	}

	@Override
	public void createLP() {
		createLP(true);
	}
	
	@Override
	public void createLP(boolean relax) {
		//Dual without relaxation not possible
		if(!relax) {
			throw new IllegalArgumentException("Unrelaxed model not available for dual program.");
		}
		try {
			env   = new GRBEnv();
			model = new GRBModel(env);
//			GRBVar[]   y     = new GRBVar[data.getNumPlayers()+1];
			
			// Create variables
			for (int i = 0; i < data.getNumPlayers()+1; i++) {
				model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "y_"+i);
			}
			
			// Integrate new variables
			model.update();
			
			// Often used variable
			GRBVar y_m = model.getVarByName("y_"+data.getNumPlayers());
			
			// Set objective: minimize sum v_i + m v_i+1
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < data.getNumPlayers(); i++) {
				expr.addTerm(1.0, model.getVarByName("y_"+i));
			}
			expr.addTerm(data.getNumSubsets(), y_m);
			model.setObjective(expr, GRB.MINIMIZE);
			
			// Add constraint: y_i + j y_i+1 >= v_i(j)
			for (int i = 0; i < data.getNumPlayers(); i++) {
				GRBVar y_i = model.getVarByName("y_"+i);
				for (int j = 1; j <= data.getNumSubsets(); j++) {
					double val = data.getValuation(i, j);
					if(val == 0) continue; //No need to add constraint if valuation = 0
					
					expr = new GRBLinExpr();
					expr.addTerm(1.0, y_i);
					expr.addTerm(j, y_m);
					model.addConstr(expr, GRB.GREATER_EQUAL, val, "c_"+i+"-"+j);
					
//					System.err.println("here c_"+i+"-"+j);
//					break; //only one valuation per player (to simulate demand oracle)
				}
			}
		} catch (GRBException e) {
			//e.printStackTrace();
			log.severe(e.getMessage());
		}
	}

	@Override
	public void solveLP() {
		//For compatibility reason: in case createLP hasn't been called before, call it here
		if(model == null) {
			createLP();
		}
		try {
			// Optimize model
			model.optimize();
			
			// Check for multiple solutions
			int solcount = model.get(GRB.IntAttr.SolCount);
			if(solcount > 1) { //TODO: does this ever happen?
				System.err.println("Multiple solutions found: " + solcount);
			}

			// Convert solution to double array and save used solutions
			boolean fractional_found = false;
			for (int i = 0; i < data.getNumPlayers(); i++) {
//				y_double[i] = y[i].get(GRB.DoubleAttr.X);
				for (int j = 1; j <= data.getNumSubsets(); j++) {
					// Get primal solution (@see http://www.gurobi.com/documentation/5.6/reference-manual/pi)
					GRBConstr c = model.getConstrByName("c_"+i+"-"+j);
					if(c == null) continue; //Constraint doesn't exist
					double x = c.get(GRB.DoubleAttr.Pi);
					if(x > 0) {
						sol_used.put(i, j);
						sol_values.put(i+"-"+j, x);
						//Test if there is at least one fractional solution
						if(!fractional_found && x != FastMath.rint(x)) {
							fractional_found = true;
						}
					}
				}
			}
//			y_double[data.getNumPlayers()] = y[data.getNumPlayers()].get(GRB.DoubleAttr.X);
			obj = model.get(GRB.DoubleAttr.ObjVal);
			
			//Warning of solution is already integral
			if(!fractional_found) {
				log.warning("Solution of LP relaxation is integral!");
			}
			
			//Recommended by Gurobi for memory management
			model.dispose();
			env.dispose();
			
			//DEBUG
			log.info(toString());
		} catch (GRBException e) {
			//e.printStackTrace();
			log.severe(e.getMessage());
		}
	}
	
	/**
	 * Saves the model to file for later use.
	 * <p>
	 * Useful if out of memory, because reading model from file takes less memory.
	 * @param folder folder name to save knp_gurobi_dual.lp file.
	 */
	public void saveToFile(String folder) {
		try {
			model.update();
			model.write(folder + "/knp_gurobi_dual.lp");
		} catch (GRBException e) {
			//e.printStackTrace();
			log.severe(e.getMessage());
		}
	}
	
	/**
	 * Reads the previously saved file from <code>saveToFile</code> method.
	 * @param folder folder name to read knp_gurobi_dual.lp file from.
	 */
	public void readFromFile(String folder) {
		try {
			env   = new GRBEnv();
			model = new GRBModel(env, folder + "/knp_gurobi_dual.lp"); // Read from file
		} catch (GRBException e) {
			//e.printStackTrace();
			log.severe(e.getMessage());
		}
	}
	
	@Override
	public double getResult(int i, int j) {
		if(sol_values.containsKey(i+"-"+j)) {
			return sol_values.get(i+"-"+j);
		}
		return 0;
	}

	@Override
	public ListMultimap<Integer, Integer> getSolUsed() {
		return sol_used;
	}
	
	@Override
	public String toString() {
		if(to_string.length() > 0) return to_string;
		
//		to_string += "Gurobi dual solution:\n";
//		for (int i = 0; i < data.getNumPlayers()+1; i++) {
//			to_string += String.format("y_%d=%f ", i, y_double[i]);
//		}
		to_string += "\nGurobi LP dual solution (#"+sol_used.size()+"):\n";
		for (Iterator<Integer> players = sol_used.keySet().iterator(); players.hasNext();) {
			Integer i = players.next();
			to_string += data.getPlayer(i).getName() + " gets";
			
			//Iterate over all possible subsets
			for (Iterator<Integer> sets = sol_used.get(i).iterator(); sets.hasNext();) {
				Integer j = sets.next();
				to_string += String.format(" x(%5d)=%7.5f", j, getResult(i,j));
			}
			to_string += "\n";
		}
		to_string += String.format("Objective value / Optimal social welfare: %f", obj);
		return to_string;
	}

	@Override
	public double getObjective() {
		return obj;
	}
}
