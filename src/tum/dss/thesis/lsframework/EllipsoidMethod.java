package tum.dss.thesis.lsframework;

import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.math3.util.FastMath;
import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Matrix;
import org.jscience.mathematics.vector.SparseMatrix;
import org.jscience.mathematics.vector.Vector;

import tum.dss.thesis.MatrixHelper;
import tum.dss.thesis.PrimalDecompositionInterface;
import tum.dss.thesis.SeparationOracleInterface;

/**
 * Basic ellipsoid method basically suitable for all kinds of problems.
 * Only minor adjustments have been done for LS framework.
 * @author Florian Stallmann
 *
 */
public class EllipsoidMethod {
	private static final Logger log = Logger.getLogger( EllipsoidMethod.class.getName() );
	
	/** Separation oracle to ask whether center is in polytope */
	private final SeparationOracleInterface oracle;
	
	/** Dimension, set when constructing the class */
	private final int n;
	
	/** Constant for central cut */
	public static final int CUT_CENTRAL = 1;
	/** Constant for deep cut */
	public static final int CUT_DEEP = 2;
	/** Constant for shallow cut */
	public static final int CUT_SHALLOW = 4;
	
	/** Constant for feasibility mode */
	public static final int MODE_FEASIBILITY = 1;
	/** Constant for maximize mode */
	public static final int MODE_MAXIMIZE = 2;
	/** Constant for minimize mode */
	public static final int MODE_MINIMIZE = 4;

	/** Epsilon for threshold */
	private FloatingPoint eps = FloatingPoint.valueOf(1e-5);
	
	/** Save best found objective value */
	private FloatingPoint best_obj;
	/** Save best found center */
	private Vector<FloatingPoint> best_a;
	
	/** Loop variable */
	private int i;
	/** Max number of iterations */
	private int N;
	/** Radius to begin with */
	private double R;
	/** Radius^2 to begin with (for precision reasons) */
	private double R_squared;
	/** Save initial center */
	private Matrix<FloatingPoint> initial_center;
	
	/** Type of cut (default: central) */
	private int cut = EllipsoidMethod.CUT_CENTRAL;
	
	/** Primal decomposition model, this is needed to test feasibility */
	private PrimalDecompositionInterface decomp = null;
	/** Stop ellipsoid method as soon as the primal decomposition is feasible */
	private boolean decomp_stop_if_feasibe = false;
	/** Save first iteration in which primal was feasible */
	private int decomp_first_feasible_iter = 0;
	
	/** Will save string representation of object to improve performance */
	private String to_string = "";
	
	/**
	 * Initializes method with oracle and column matrix as center.
	 * @param oracle separation oracle.
	 * @param center initial center.
	 */
	public EllipsoidMethod(final SeparationOracleInterface oracle, final Matrix<FloatingPoint> center) {
		if(center.getNumberOfColumns() != 1 || center.getNumberOfRows() < 1) {
			throw new IllegalArgumentException("Center is not a vector");
		}
		
		this.oracle = oracle;
		n = center.getNumberOfRows();
		initial_center = center;
		
		setMaxIterations(500000); //very high number by default
		
		//setRadius( FastMath.ceil(FastMath.sqrt(center.length)) ); //rounded up length of longest diagonal in a hypercube, see: http://en.wikipedia.org/wiki/Hypercube
		//setRadius( FastMath.sqrt(center.length) );
		
		//Set radius directly to avoid sqrt(r) and later r^2 which is less precise
		R_squared = n;
		R = FastMath.sqrt(n);
		
		//DEBUG
		log.info("Ellipsoid method initialized with "+n+" radius " + R + " and center " + center.toString());
	}
	
	/**
	 * Initializes method with oracle and vector as center.
	 * @param oracle separation oracle.
	 * @param center initial center.
	 */
	public EllipsoidMethod(SeparationOracleInterface oracle, Vector<FloatingPoint> center) {
		this(oracle, MatrixHelper.createColumnMatrix(center));
	}
	
	/**
	 * Initialize with oracle and number of dimensions, zero vector will be center.
	 * @param oracle    separation oracle.
	 * @param dimension dimension of initial center vector.
	 */
	public EllipsoidMethod(SeparationOracleInterface oracle, int dimension) {
		this(oracle, MatrixHelper.createColumnMatrix(dimension));
	}
	
	/**
	 * Overrides calculated radius (default square root of dimension).
	 * @param radius initial radius for ball.
	 */
	public void setRadius(final double radius) {
		R = radius;
		R_squared = R*R;
	}
	/**
	 * Overrides maximum number of iterations (default 500.000).
	 * @param iter maximum number of iterations.
	 */
	public void setMaxIterations(final int iter) {
		N = iter;
	}
	
	/**
	 * Sets type of cut (central, deep, shallow).
	 * @param cut type of cut
	 * @see EllipsoidMethod#CUT_CENTRAL
	 * @see EllipsoidMethod#CUT_SHALLOW
	 * @see EllipsoidMethod#CUT_DEEP
	 */
	public void setCut(final int cut) {
		this.cut = cut;
	}
	
	/**
	 * Sets epsilon threshold (default 1e-5).
	 * @param eps threshold.
	 */
	public void setEpsilon(final double eps) {
		this.eps = FloatingPoint.valueOf(eps);
	}
	
	/**
	 * Sets primal LP of the decomposition that is called to test feasibility each time a new solution is found.
	 * @param decomp primal model
	 */
	public void setPrimalDecomposition(final PrimalDecompositionInterface decomp) {
		this.decomp = decomp;
	}
	
	/**
	 * Set to true if the method should end if primal decomposition is feasible.
	 * @param stop_feas true to stop if feasible. 
	 */
	public void setStopIfFeasible(final boolean stop_feas) {
		decomp_stop_if_feasibe = stop_feas;
	}
	
	/**
	 * Default: Start algorithm in maximization mode.
	 */
	public void startAlgorithm() {
		startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
	}
	
	/**
	 * Backwards compatibility: Allow boolean for maximization.
	 * @param optimize set to true for maximization instead of feasibility mode.
	 * @deprecated Use {@link #startAlgorithm(boolean)} with <code>EllipsoidMethod.MODE_MAXIMIZE</code> as parameter instead.
	 */
	public void startAlgorithm(final boolean optimize) {
		startAlgorithm((optimize ? EllipsoidMethod.MODE_MAXIMIZE : EllipsoidMethod.MODE_FEASIBILITY));
	}
	
	/**
	 * Run ellipsoid method to find feasible or optimal solution.
	 * @param mode whether feasible, minimal or maximal solution should be found.
	 * @see EllipsoidMethod#MODE_MAXIMIZE
	 * @see EllipsoidMethod#MODE_MINIMIZE
	 * @see EllipsoidMethod#MODE_FEASIBILITY
	 */
	public void startAlgorithm(final int mode) {
		AnalysisHelper.startRuntimeMeasure(); //ANALYSIS
		
		Matrix<FloatingPoint> A, a, c, g, b, obj_cut = null;
		FloatingPoint gradient, rho = FloatingPoint.ONE, sigma = FloatingPoint.ONE, tau = FloatingPoint.ONE, xi = FloatingPoint.ONE, gamma;
		boolean optimize = (mode == EllipsoidMethod.MODE_MAXIMIZE || mode == EllipsoidMethod.MODE_MINIMIZE);
		to_string = "";
		
		System.out.println("Optimize a model using ellipsoid with initial radius " + R + " using a "
				+ getCutName() + " cut");
		
		//DEBUG
		log.fine("Starting ellipsoid method with initial radius " + R);
		
		//If optimize get objective cut because it will never change within the iterations
		if(optimize) {
			obj_cut = MatrixHelper.createColumnMatrix(oracle.getObjectiveCut());
			if(mode == EllipsoidMethod.MODE_MAXIMIZE) {
				//times -1 if max problem
				obj_cut = obj_cut.times(FloatingPoint.ONE.opposite());
			}
		}
		
		//Helper variables for high precision
		FloatingPoint fp_n     = FloatingPoint.valueOf(n);
		FloatingPoint fp_np1   = fp_n.plus(FloatingPoint.ONE);
		FloatingPoint fp_nsq   = fp_n.pow(2);
		FloatingPoint fp_np1sq = fp_np1.pow(2);
		
		//Parameters for different cuts
		if(cut == EllipsoidMethod.CUT_CENTRAL) {
			//central cut
			rho = fp_np1.inverse();
			sigma = fp_nsq.divide(fp_nsq.minus(FloatingPoint.ONE));
			tau = FloatingPoint.valueOf("2").divide(fp_np1);
			xi = FloatingPoint.valueOf("4").times(fp_np1sq).inverse().plus(FloatingPoint.ONE);
		} else if (cut == EllipsoidMethod.CUT_SHALLOW) {
			//shallow cut
			rho = fp_np1sq.inverse();
			sigma = fp_n.pow(3).times(fp_n.plus(FloatingPoint.valueOf("2"))).divide(fp_np1.pow(3).times(fp_n.minus(FloatingPoint.ONE)));
			tau = FloatingPoint.valueOf("2").divide(fp_n.times(fp_np1));
			xi = FloatingPoint.valueOf("2").times(fp_nsq).times(fp_np1sq).inverse().plus(FloatingPoint.ONE);
		} else {
			//deep cut
			xi = FloatingPoint.valueOf("4").times(fp_np1sq).inverse().plus(FloatingPoint.ONE);
		}

		//Initialize matrix for ellipsoid and center
		FloatingPoint[] diag = new FloatingPoint[n];
		Arrays.fill(diag, FloatingPoint.valueOf(R_squared));
		A = DenseMatrix.valueOf(SparseMatrix.valueOf(DenseVector.valueOf(diag), FloatingPoint.ZERO));
		a = initial_center.copy();
		best_obj = null;
		best_a = null;

//		// DEBUG
//		System.out.println("A:" + A);
//		System.out.println("a:" + a);
//		System.out.println("rho:" + rho);
//		System.out.println("sigma:" + sigma);
//		System.out.println("tau:" + tau);
//		System.out.println("xi:" + xi);
		
		i = 1;
		while(i <= N) {
			try {
				//Oracle: either returns cutting plane or point in polytope
				if( !oracle.ask(a.getColumn(0)) ){
					//not in polytope
					
					//constraint cut
					c = MatrixHelper.createColumnMatrix(oracle.getConstraintCut());
					gamma = oracle.getGammaValue();
					
					//DEBUG
					log.finer("Ellipsoid iteration " + i + ": Constraint cut with " + c);
					AnalysisHelper.logConstraintCut(i); //ANALYSIS
					
					//Test feasibility if primal model was given, model was not yet feasible, and new solution were found
					if(		   decomp != null
							&& decomp_first_feasible_iter == 0
							&& oracle.lastWzWasNewSolution()) {
						log.finer("Ellipsoid internal feasibility check of decomposition started.");
						//Model needs to be recreated every time
						decomp.createLP();
						//Test run of model
						if(decomp.testFeasibility()) {
							decomp_first_feasible_iter = i;
							log.info("Decomposition proved to be feasible in iteration " + i);
							//Should ellipsoid method stop now?
							if(decomp_stop_if_feasibe) {
								AnalysisHelper.logFinalIteration(i, "decomposition is feasible"); //ANALYSIS
								break;
							}
						}
					}
				} else if (optimize) {
					//point in polytope: sliding objective
					c = obj_cut;
					gamma = (mode == EllipsoidMethod.MODE_MAXIMIZE ? oracle.getGammaValue().opposite() : oracle.getGammaValue());
					
					//Save best solution found so far
					if(best_obj == null || 
					  (mode == EllipsoidMethod.MODE_MAXIMIZE && oracle.getGammaValue().isGreaterThan(best_obj)) ||
					  (mode == EllipsoidMethod.MODE_MINIMIZE && oracle.getGammaValue().isLessThan(best_obj)) ){
						best_obj = oracle.getGammaValue();
						best_a   = a.getColumn(0);
					}
					
					//Check threshold using relative error
					//optimal value = 1 in LS Framework, gamma=obj if objective cut
					//this threshold always applies in first iteration because starting point is "optimal"
//					FloatingPoint rel_error = gamma.abs().minus(FloatingPoint.ONE).abs();
//					if(rel_error.isLessThan(eps)) {
//						AnalysisHelper.logFinalIteration(i, "threshold reached"); //ANALYSIS
//						//DEBUG: stop criteria
//						log.info("Threshold reached in iteration " + i + "!");
//						break;
//					}
					
					//DEBUG
					log.finer("Ellipsoid iteration " + i + ": Objective cut with " + c);
				} else {
					//point in polytope is enough (feasibility)
					best_a = a.getColumn(0);
					best_obj = oracle.getGammaValue();
					break;
				}
				
				//Life signal to console every 50th iteration
				if((i % 50) == 0) {
					System.out.println("Ellipsoid still running in iteration "+i+"...");
				}
				
				//calc gradient, but not yet square root, first verify if positive
				gradient = c.transpose().times(A).times(c).get(0, 0);
				
				//Check for negative or NaN gradient value
				if(gradient.isNaN() || gradient.isNegative()) {
					AnalysisHelper.logFinalIteration(i, "gradient not positive"); //ANALYSIS
					//DEBUG: NaN value
					log.severe("Gradient is not positive in iteration " + i + "!");
					break;
				}
				
				//square root only if check for positive value passed
				gradient = gradient.sqrt();
				
				//Check threshold
				if(optimize && gradient.isLessThan(eps)) {
					AnalysisHelper.logFinalIteration(i, "threshold reached"); //ANALYSIS
					//DEBUG: stop criteria
					log.info("Threshold reached in iteration " + i + "!");
					break;
				}
				
				//DEBUG: Last iteration
				if(i == N) {
					AnalysisHelper.logFinalIteration(i, "reached N"); //ANALYSIS
					//Reached last iteration prematurely
					log.warning("Reached final iteration " + i + "!");
				}
				
				//Calculate value for deep cut, get gamma value from oracle
				if(cut == EllipsoidMethod.CUT_DEEP) {
					FloatingPoint alpha = c.transpose().times(a).get(0,0).minus(gamma).divide(gradient);
					rho = fp_n.times(alpha).plus(FloatingPoint.ONE).divide(fp_np1);
					sigma = fp_nsq.times(FloatingPoint.ONE.minus(alpha.pow(2))).divide(fp_nsq.minus(FloatingPoint.ONE));
					tau = FloatingPoint.valueOf("2").times(FloatingPoint.ONE.plus(alpha.times(fp_n))).divide(fp_np1.times(alpha.plus(FloatingPoint.ONE)));
				}
				
				g = c.times(gradient.inverse());
				b = A.times(g);

				a = a.minus(b.times(rho));
				A = A.minus(b.times(b.transpose()).times(tau)).times(xi.times(sigma));

				// DEBUG
//				System.out.println("A:" + A);
//				System.out.println("a:" + a);
//				System.out.println("g:" + g);
//				System.out.println("b:" + b);
//				System.out.println("gradient:" + gradient);
				
				//DEBUG: will throw exception if A is not positive definite
//				CholeskyDecomposition test = new CholeskyDecomposition(A);
//				EigenDecomposition test = new EigenDecomposition(A);
//				System.err.println( Arrays.toString(test.getRealEigenvalues()) );
			} catch (Exception e) {
				e.printStackTrace();
				log.severe("Iteration " + i + ": " + e.getMessage());
			}
			i++;
		}
		
		AnalysisHelper.stopRuntimeMeasure(); //ANALYSIS
		
		//DEBUG
		log.info(toString());
	}
	
	/**
	 * Gets the result as double array.
	 * @return best found feasible center.
	 */
	public double[] getResult() {
		return MatrixHelper.vectorToDouble(best_a);
	}
	
	/**
	 * Gets the objective value as double.
	 * @return best found objective value.
	 */
	public double getObjectiveValue() {
		return best_obj.doubleValue();
	}
	
	/**
	 * Gets the name of currently used cut.
	 * @return string to describe type of cut.
	 */
	public String getCutName() {
		return (cut == EllipsoidMethod.CUT_CENTRAL ? "central" : (cut == EllipsoidMethod.CUT_SHALLOW ? "shallow" : "deep"));
	}
	
	/**
	 * Gets the maximum number of iterations when ellipsoid is stopped.
	 * @return maximum iterations.
	 */
	public int getMaxIterations() {
		return N;
	}
	
	/**
	 * Gets epsilon threshold.
	 * @return threshold.
	 */
	public double getEpsilon() {
		return eps.doubleValue();
	}
	
	/**
	 * Gets the initial radius.
	 * @return intial radius.
	 */
	public double getRadius() {
		return R;
	}
	
	/**
	 * Gets the first feasible iteration of primal decomposition model.
	 * <p>
	 * Returns 0 if feasibility was not proved at all.
	 * @return iteration of first proven feasibility of decomposition.
	 */
	public int getFirstFeasibleIteration() {
		return decomp_first_feasible_iter;
	}
	
	@Override
	public String toString() {
		if(to_string.length() > 0) return to_string;
		if(best_obj == null) return "No solution found";
		
		to_string = "Ellipsoid solution (best feasible point): " + Arrays.toString( getResult() );
		to_string += String.format("%nObjective value: %f", getObjectiveValue());
		return to_string;
	}
	
}
