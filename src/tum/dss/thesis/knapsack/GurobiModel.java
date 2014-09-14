package tum.dss.thesis.knapsack;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.Iterator;
import java.util.logging.Logger;

import tum.dss.thesis.LinearProgramInterface;
import tum.dss.thesis.MechanismDataInterface;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Gurobi model to solve the relaxed IP (KNP).
 * @author Florian Stallmann
 *
 */
public class GurobiModel implements LinearProgramInterface {
	private static final Logger log = Logger.getLogger( GurobiModel.class.getName() );
	
	/** Mechanism data to run gurobi on */
	protected MechanismDataInterface data;
	
	/** Gurobi solution matrix */
	protected double[][] x_double;
	
	/** Save index of solutions where x<sub>i,j</sub> &gt; 0 */
	protected ListMultimap<Integer, Integer> sol_used = LinkedListMultimap.create();
	
	/** Objective value */
	protected double     obj;
	
	/** Will save string representation of object to improve performance */
	private String to_string = "";
	
	/** Gurobi environment */
	private GRBEnv     env ;
	/** Gurobi model */
	private GRBModel   model;
	/** Gurobi variables */
	private GRBVar[][] x;
	
	/**
	 * Initializes without data.
	 */
	public GurobiModel() {
	}
	
	/**
	 * Initializes with data.
	 * @param data mechanism data.
	 */
	public GurobiModel(final MechanismDataInterface data) {
		this();
		setData(data);
	}
	
	@Override
	public void setData(final MechanismDataInterface data) {
		this.data  = data;
		sol_used.clear();
		to_string = "";
		x_double = new double[data.getNumPlayers()][data.getNumSubsets()+1];
	}

	@Override
	public void createLP() {
		createLP(true);
	}
	
	@Override
	public void createLP(boolean relax) {
		try {
			env   = new GRBEnv();
			model = new GRBModel(env);
			x     = new GRBVar[data.getNumPlayers()][data.getNumSubsets()+1];
			
			// Create variables
			for (int i = 0; i < data.getNumPlayers(); i++) {
				for (int j = 1; j <= data.getNumSubsets(); j++) {
					if(relax) {
						x[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_"+i+"-"+j);
					} else {
						x[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.BINARY, "x_"+i+"-"+j);
					}
				}
			}
			
			// Integrate new variables
			model.update();
			
			// Set objective: maximize sum v_ij
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < data.getNumPlayers(); i++) {
				for (int j = 1; j <= data.getNumSubsets(); j++) {
					expr.addTerm(data.getValuation(i, j), x[i][j]);
				}
			}
			model.setObjective(expr, GRB.MAXIMIZE);
			
			// Add constraint: sum_j x[][] <= 1
			for (int i = 0; i < data.getNumPlayers(); i++) {
				expr = new GRBLinExpr();
				for (int j = 1; j <= data.getNumSubsets(); j++) {
					expr.addTerm(1.0, x[i][j]);
				}
				model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "c1-"+i);
			}
			
			// Add constraint: sum_ij x[][]*j <= m
			expr = new GRBLinExpr();
			for (int i = 0; i < data.getNumPlayers(); i++) {
				for (int j = 1; j <= data.getNumSubsets(); j++) {
					expr.addTerm(j, x[i][j]);
				}
			}
			model.addConstr(expr, GRB.LESS_EQUAL, data.getNumSubsets(), "c2");
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
			for (int i = 0; i < data.getNumPlayers(); i++) {
				for (int j = 1; j <= data.getNumSubsets(); j++) {
					x_double[i][j] = x[i][j].get(GRB.DoubleAttr.X);
					if(x_double[i][j] > 0) {
						sol_used.put(i, j);
					}
				}
			}
			obj = model.get(GRB.DoubleAttr.ObjVal);
			
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
	
	@Override
	public double getResult(int i, int j) {
		return x_double[i][j];
	}

	@Override
	public ListMultimap<Integer, Integer> getSolUsed() {
		return sol_used;
	}
	
	@Override
	public String toString() {
		if(to_string.length() > 0) return to_string;
		
		to_string = "Gurobi LP solution:\n";
//		for (int i = 0; i < data.getNumPlayers(); i++) {
//			to_string += data.getPlayer(i).getName() + " gets";
//			for (int j = 1; j <= data.getNumSubsets(); j++) {
//				if(x_double[i][j] > 0) {
//					to_string += String.format(" x(%d)=%f", j, x_double[i][j]);
//				}
//			}
//			to_string += "\n";
//		}
		
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
