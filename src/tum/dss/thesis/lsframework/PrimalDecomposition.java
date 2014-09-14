package tum.dss.thesis.lsframework;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math3.util.FastMath;

import tum.dss.thesis.PlayerInterface;
import tum.dss.thesis.PrimalDecompositionInterface;

/**
 * Primal LP of the convex decomposition as Gurobi Model.
 * @author Florian Stallmann
 *
 */
public class PrimalDecomposition implements PrimalDecompositionInterface {
	private static final Logger log = Logger.getLogger( PrimalDecomposition.class.getName() );
	
	/** Ellipsoid data containing the integer solutions */
	protected final EllipsoidMethodData ell_data;
	
	/** Gurobi solution vector, later treated as probabilities */
	protected double[] lambda;
	
	/** Save index of solutions where lambda &gt; 0 */
	protected List<Integer> sol_used = new ArrayList<Integer>();
	
	/** Objective value */
	protected double obj;
	
	/** Calculated expected social welfare (should be optimal/alpha) */
	protected double expected_social_welfare = 0;
	
	/** Integrality gap of oracle */
	protected double integrality_gap = 1;
	
	/** Maximum social welfare of LP relaxation */
	protected double max_welfare = 0;

	/** Will save string representation of object to improve performance */
	private String to_string = "";
	
	/** Gurobi environment */
	private GRBEnv     env ;
	/** Gurobi model */
	private GRBModel   model;
	/** Gurobi variables */
	private GRBVar[] x;

	/**
	 * Initializes model with ellipsoid data, i.e. the found integer solutions.
	 * @param data ellipsoid method data.
	 */
	public PrimalDecomposition(final EllipsoidMethodData data) {
		this.ell_data = data;
	}
	
	@Override
	public boolean testFeasibility() {
		if(model == null) {
			return false;
		}
		int status = 0;
		try {
			//Trial run to retrieve status code
			model.optimize();
			status = model.get(GRB.IntAttr.Status);
			if(status == GRB.OPTIMAL) {
				obj = model.get(GRB.DoubleAttr.ObjVal);
			} else {
				obj = 0;
			}
			//Reset model (TODO: is this necessary?)
			model.reset();
		} catch (GRBException e) {
			//e.printStackTrace();
			log.severe(e.getMessage());
		}
		//Important: objective value needs to be 1
		if(status == GRB.OPTIMAL && FastMath.abs(obj-1.0) <= 0.000001) {
			return true;
		}
		return false;
	}

	/**
	 * Creates and solves the LP model.
	 * @param integrality_gap integrality gap of the separation oracle.
	 */
	public void solveLP(final double integrality_gap) {
		//For compatibility reason: in case createLP hasn't been called before, call it here
		if(model == null) {
			setIntegralityGap(integrality_gap);
			createLP();
		}
		solveLP();
	}
	
	@Override
	public void solveLP() {
		try {
			// Optimize model
			model.optimize();
			obj = model.get(GRB.DoubleAttr.ObjVal);
			
			// Check for multiple solutions
			int solcount = model.get(GRB.IntAttr.SolCount);
			if(solcount > 1) { //TODO: does this ever happen?
				System.err.println("Multiple solutions found: " + solcount + ".");
			}

			//Convert solution to array and save actually used solutions
			lambda = new double[ell_data.getNumIntegerSolutions()];
			for (int i = 0; i < ell_data.getNumIntegerSolutions(); i++) {
				lambda[i] = x[i].get(GRB.DoubleAttr.X);
				if(lambda[i] > 0) {
					sol_used.add(i);
					IntegerSolution xl = ell_data.getIntegerSolution(i);
					xl.setLambda(lambda[i]);
					expected_social_welfare += xl.getWelfare() * xl.getLambda();
				}
			}
			
			//Validate that sum of lambda is 1
			if( FastMath.abs(obj-1.0) > 0.000001 ) {
				System.err.println("Decomposition validation failed: Primal objective value does not equal 1. Decomposition is not convex!");
			}
			//Validate that maximum welfare divided by integrality gap is expected welfare
			if( FastMath.abs(max_welfare / integrality_gap - expected_social_welfare) > 0.000001 ) {
				System.err.println("Decomposition validation failed: Optimal social welfare divided by alpha does not equal calculated expected social welfare!");
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
	
	@Override
	public void createLP() {
		try {
			env   = new GRBEnv();
			model = new GRBModel(env);
			x     = new GRBVar[ell_data.getNumIntegerSolutions()];
			
			// Create variables
			for (int i = 0; i < ell_data.getNumIntegerSolutions(); i++) {
				x[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "lambda_"+i);
			}
			
			// Integrate new variables
			model.update();
			
			// Set objective: minimize sum lambda_l
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < ell_data.getNumIntegerSolutions(); i++) {
				expr.addTerm(1, x[i]);
			}
			model.setObjective(expr, GRB.MINIMIZE);
			
			// Add constraint: sum_l lamda_l >= 1
			expr = new GRBLinExpr();
			for (int i = 0; i < ell_data.getNumIntegerSolutions(); i++) {
				expr.addTerm(1.0, x[i]);
			}
			model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");
//			model.addConstr(expr, GRB.EQUAL, 1.0, "c1");
			
			// Add constraint: sum_l lambda_l * x_l = x^* / alpha
			for (int j = 0; j < ell_data.getDimension()-1; j++) {
				expr = new GRBLinExpr();
				for (int i = 0; i < ell_data.getNumIntegerSolutions(); i++) {
					expr.addTerm(ell_data.getIntegerSolution(i).getSolution()[j], x[i]);
//					System.out.println(data.x_l.get(i)[j] + " * x_" + i + " = " + (data.x_star[j]/integrality_gap));
				}
				model.addConstr(expr, GRB.EQUAL, (ell_data.x_star[j]/integrality_gap), "c2-"+j);
			}
		} catch (GRBException e) {
			//e.printStackTrace();
			log.severe(e.getMessage());
		}
	}
	
	@Override
	public double[] getResult() {
		return lambda;
	}
	
	@Override
	public String toString() {
		if(to_string.length() > 0) return to_string;
		
		to_string = "Decomposition solution:\nMeaning of solution vector: [";
		//meaning of integer solution vector
		for (int i = 0; i < ell_data.getDimension()-1; i++) {
			PlayerInterface pl = ell_data.getPlayer(ell_data.whichPlayer(i));
			int j = ell_data.whichItemSubset(i);
			to_string += pl.getName() + " gets set {" + ell_data.getItemsetHandler().getSet(j) + "}, ";
		}
		to_string = to_string.substring(0, to_string.length()-2); //remove last comma
		to_string += "]\nProbabilities and corresponding integer solution:\n";
		//lambda and according integer solution vector
		for (int i = 0; i < sol_used.size(); i++) {
			int index_of_solution = sol_used.get(i);
			IntegerSolution xl = ell_data.getIntegerSolution(index_of_solution);
			to_string += String.format("lambda_%d = %f and welfare = %f and x_l = %s%n", index_of_solution, xl.getLambda(), xl.getWelfare(), xl.toString());
		}
		to_string += "lambda_else = 0\nUsing " + sol_used.size() + " out of " + ell_data.getNumIntegerSolutions() + " integer solutions.";
		//Expected social welfare
		to_string += String.format("%nExpected social welfare: %f", expected_social_welfare);
		//Objective value
		to_string += String.format("%nObjective value of primal LP: %f", obj);
		return to_string;
	}

	@Override
	public List<Integer> getSolUsed() {
		return sol_used;
	}

	@Override
	public double getExpectedSocialWelfare() {
		return expected_social_welfare;
	}

	@Override
	public double getObjective() {
		return obj;
	}

	@Override
	public void setIntegralityGap(final double integrality_gap) {
		this.integrality_gap = integrality_gap;
	}

	@Override
	public void setMaxSocialWelfare(double welfare) {
		max_welfare = welfare;
	}
}
