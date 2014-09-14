package tum.dss.thesis.lsframework;

import org.jscience.mathematics.number.FloatingPoint;

import tum.dss.thesis.LinearProgramInterface;
import tum.dss.thesis.MechanismDataInterface;
import tum.dss.thesis.MechanismInterface;
import tum.dss.thesis.PrimalDecompositionInterface;
import tum.dss.thesis.SeparationOracleInterface;

/**
 * Class with various static methods to run a single iteration of the LS framework step by step.
 * @author Florian Stallmann
 * 
 */
public abstract class AbstractMechanism implements MechanismInterface {
	/** Identifier of the mechanism (used as folder name) */
	public String id = "";
	
	/** Counting the calls on runMechanism (used as id for filenames) */
	public int counter = 0;
	
	/** Original data with players, valuations and items/subsets */
	public MechanismDataInterface data;
	
	/** Gurobi LP Relaxation model */
	public LinearProgramInterface lp;
	
	/** Data for ellipsoid method */
	public EllipsoidMethodData ell_data;
	
	/** Separation oracle */
	public SeparationOracleInterface oracle;
	
	/** Primal decomposition model */
	public PrimalDecompositionInterface decomp;
	
	/** Save if LP is already solved */
	protected boolean lp_solved = false;
	
	/** Save if data is already imported to ellipsoid data */
	protected boolean data_imported = false;
	
	/** Ellipsoid config: cut */
	protected int cut = EllipsoidMethod.CUT_CENTRAL;
	
	/** Ellipsoid config: epsilon */
	protected double eps = 0;
	
	/** Ellipsoid config: radius */
	protected double radius = 0;
	
	/** Ellipsoid config: interations */
	protected int n = 0;
	
	/** Mechanism config: stop if decomposition feasible */
	protected boolean stop_if_feasible = false;
	
	/** Mechanism config: add valid solutions due to packing property */
	protected boolean add_packing_property_solutions = false;
	
	/** Mechanism config: use objective value instead of 1 as gamma for deep cut */
	protected boolean use_objective_as_gamma = false;
	
	/** Mechanism config: changes initial center, default 0 is original center proposed by paper */
	protected int type_of_initial_center = 0;
	
	/** Mechanism config: overwrite original integrality gap, default 0 will ask the oracle for its gap */
	protected double overwrite_int_gap = 0;
	
	/** Mechanism config: minimize deviation when solving the decomposition */
	protected boolean min_deviation = false;
	
	@Override
	public void setData(MechanismDataInterface data) {
		this.data = data;
		lp_solved = false;
		data_imported = false;
		lp.setData(data);
	}

	@Override
	public void setCut(int cut) {
		this.cut = cut;
	}

	@Override
	public void setEpsilon(double eps) {
		this.eps = eps;
	}

	@Override
	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Override
	public void setMaxIterations(int n) {
		this.n = n;
	}

	@Override
	public void setIdenfitier(String id) {
		this.id = id;
		
	}
	
	@Override
	public void setStopIfFeasible(boolean feas) {
		stop_if_feasible = feas;
	}
	
	@Override
	public void setManualIntegerSolutions(boolean add) {
		add_packing_property_solutions = add;
	}
	
	@Override
	public void setObjectiveAsGamma(boolean deep_gamma) {
		use_objective_as_gamma = deep_gamma;
	}
	
	@Override
	public void setInitialCenter(int center_type) {
		type_of_initial_center = center_type;
	}
	
	@Override
	public void setOverwriteGap(double int_gap) {
		overwrite_int_gap = int_gap;
	}
	
	@Override
	public void setMinimizeDeviation(boolean decomp_min_dev) {
		min_deviation = decomp_min_dev;
	}
	
	/**
	 * Sets precision for floating points and create LP.
	 */
	public AbstractMechanism() {
		// Arbitrary precision floating point
		FloatingPoint.setDigits(64); //default is 20 digits
		
		// Force subclass to provide LP
		lp = createLP();
	}
	
	/**
	 * Step 1: Solve the LP, e.g. using Gurobi.
	 * <p>
	 * If this step was performed before it will just output the saved solution.
	 */
	public void step1SolveLp() {
		System.out.println("############################################");
		System.out.println("# Step 1: Solve LP using Solver            #");
		System.out.println("############################################");
		
		//Run only once per data set
		if(!lp_solved) {
			// Get allocation for relaxed IP using Gurobi
			lp.solveLP();
			lp_solved = true;
		}
		
		// Output LP solution
		System.out.println(lp);
		System.out.println("\n\n");
	}
	
	/**
	 * Step 2: Solve the dual of the convex decomposition using the ellipsoid method with central cut as default.
	 */
	public void step2SolveDual() {
		step2SolveDual(cut);
	}
	
	/**
	 * Step 2: Solve the dual of the convex decomposition using the ellipsoid method with specified cut.
	 * <p>
	 * This method will import the LP result from step 1, initialize the oracle and the
	 * primal of the decomposition, set all parameters and finally start the ellipsoid method.
	 * In the end it will output all found integer solutions and some statistics.
	 * @param cut ellipsoid cut.
	 * @see EllipsoidMethod#CUT_CENTRAL
	 * @see EllipsoidMethod#CUT_DEEP
	 * @see EllipsoidMethod#CUT_SHALLOW
	 */
	public void step2SolveDual(final int cut) {
		System.out.println("############################################");
		System.out.println("# Step 2: Solve dual using Ellipsoid Meth. #");
		System.out.println("############################################");
		
		// Prepare data for convex decomposition
		if(!data_imported) {
			ell_data = new EllipsoidMethodData(data);
			ell_data.importLPResults(lp);
			data_imported = true;
		} else {
			ell_data.clearSolutions();
		}
		
		// Prepare primal for feasiblity check of decomposition
		if(min_deviation) {
			decomp = new PrimalDecompDeviation(ell_data);
		} else {
			decomp = new PrimalDecomposition(ell_data);
		}
		decomp.setMaxSocialWelfare(lp.getObjective());
		decomp.setIntegralityGap( (overwrite_int_gap > 0 ? overwrite_int_gap : oracle.getIntegralityGap()) );
		
		// Dual of convex decomposition (Ellipsoid and Greedy)
		oracle.setData(ell_data);
		oracle.setObjectiveAsGamma(use_objective_as_gamma);
		EllipsoidMethod ell = new EllipsoidMethod(oracle, ell_data.getInitialCenter(type_of_initial_center));
		
		AnalysisHelper.instance_ell    = ell; //ANALYSIS
		AnalysisHelper.instance_primal = decomp; //ANALYSIS
		
		//Set parameters
		ell.setPrimalDecomposition(decomp);
		ell.setStopIfFeasible(stop_if_feasible);
		ell.setCut(cut);
		if(eps > 0)    { ell.setEpsilon(eps); }
		if(radius > 0) { ell.setRadius(radius); }
		if(n > 0)      { ell.setMaxIterations(n); }
		
		//Add integer solution due to packing property
		if(add_packing_property_solutions) {
			ell_data.addManualIntegerSolutions(true);
		}
		//Test feasibility
		decomp.createLP();
		boolean feas = decomp.testFeasibility();
		if(feas) {
			System.out.println("Decomp is feasible before running ellipsoid!");
			AnalysisHelper.logFinalIteration(0, "no need to even start"); //will be overwritten if ellipsoid is still started
		}
		
		//Only start ellipsoid if the primal is either not feasible or it is feasible but stop is not desired in that case
		if(!(feas && stop_if_feasible)) {
			//start ellipsoid method
			ell.startAlgorithm();
			
			// Output ellipsoid result
			System.out.println( ell );
		}
		
		// Output solutions
//		System.out.println( String.format("Objective value: %f", ell_data.getObjectiveValue(ell.getCurrentCenter(), oracle.getIntegralityGap()).doubleValue()) );
		System.out.println("Integral solutions (#" + ell_data.getNumIntegerSolutions() + "):");
		String intsol = "";
		for (int i = 0; i < ell_data.getNumIntegerSolutions(); i++) {
			intsol += ell_data.getIntegerSolution(i) + ", ";
		}
		System.out.println(intsol.substring(0, intsol.length()-2));
		System.out.println("\n\n");
	}
	
	/**
	 * Step 3: Solve the primal of the convex decomposition using any solver, e.g. Gurobi.
	 * <p>
	 * This method will validate that the optimal social welfare divided by the integrality gap
	 * equals the expected social welfare of all integer solutions calculated after the primal is solved.
	 * @see PrimalDecomposition#getExpectedSocialWelfare()
	 */
	public void step3SolvePrimal() {
		System.out.println("############################################");
		System.out.println("# Step 3: Solve primal using Solver        #");
		System.out.println("############################################");
		
		// Primal of convex decomposition (already initiated in step 2)
		decomp.createLP();
		decomp.solveLP();
		
//		//Validate result by checking primal objective value
//		if( FastMath.abs(decomp.getObjective()-1.0) > 0.000001 ) {
//			System.err.println("Decomposition validation failed: Primal objective value does not equal 1. Decomposition is not convex!");
//		}
//		
//		//Validate result by comparing obj. value / alpha with expected social welfare from decomp
//		if( FastMath.abs(
//				(lp.getObjective() / oracle.getIntegralityGap()) - decomp.getExpectedSocialWelfare()
//				) > 0.000001 ) {
//			System.err.println("Decomposition validation failed: Optimal social welfare divided by alpha does not equal calculated expected social welfare!");
//		}
		
		System.out.println("\n\n");
		System.out.println("############################################");
		System.out.println("# Final Result                             #");
		System.out.println("############################################");
		
		// Output final decomposition solution
		System.out.println(decomp);
		System.out.println("\n\n");
	}
	
	/**
	 * Prints some analysis data from the special {@link AnalysisHelper}.
	 */
	public void printAnalysis() {
		// Analysis
		System.out.println("############################################");
		System.out.println("# Analysis                                 #");
		System.out.println("############################################");

		System.out.println(AnalysisHelper.getSummary());
		//System.out.println(AnalysisHelper.getHistData());
	}
	
	/**
	 * Runs one iteration of the framework for previously defined parameters.
	 * <p>
	 * This method initializes the {@link AnalysisHelper}, creates the oracle,
	 * runs all steps in order and saves the result as files.
	 */
	@Override
	public void runMechanism() {
		counter++;
		AnalysisHelper.initializeHelper(this);

		//Create new oracle for each run
		oracle = createOracle();
		
		// Framework steps
		step1SolveLp();
		step2SolveDual();
		step3SolvePrimal();
		printAnalysis();
		
		AnalysisHelper.saveToFile();
	}
	
	/**
	 * Solves the LP without relaxation to compare the integral solution with the relaxed solution.
	 */
	public void solveLpIntegral() {
		System.out.println("############################################");
		System.out.println("# Solve LP without relaxation using Solver #");
		System.out.println("############################################");
		
		// Get allocation for relaxed IP using Gurobi
		lp.setData(data);
		lp.createLP(false);
		lp.solveLP();
		
		// Output LP solution
		System.out.println(lp);
		System.out.println("\n\n");
	}
	
	@Override
	public String toString() {
		String s = "";
		s += String.format("Mechanism %s with %d players and %d subsets.", id, data.getNumPlayers(), data.getNumSubsets());
		if(lp_solved) {
			s += String.format("%nOptimal social welfare: %7.5f.", lp.getObjective());
			if(oracle != null) {
				s += String.format("%nExpected social welfare: %7.5f.", (lp.getObjective() / oracle.getIntegralityGap()));
			}
		}
		return s;
	}
}
