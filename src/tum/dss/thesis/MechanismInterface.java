package tum.dss.thesis;

import tum.dss.thesis.lsframework.EllipsoidMethod;

/**
 * Interface for an instance of a mechanism with certain parameters.
 * <p>
 * A mechanism is used to hold all settings for one iteration of a simulation. It will create
 * all instances for a specific problem and run the different steps of the ellipsoid method.
 * @author Florian Stallmann
 *
 */
public interface MechanismInterface {
	/**
	 * Creates a new separation oracle.
	 * @return separation oracle.
	 */
	public SeparationOracleInterface createOracle();
	
	/**
	 * Creates an LP model.
	 * @return linear program.
	 */
	public LinearProgramInterface createLP();
	
	/**
	 * Runs one iteration of the framework for previously defined parameters.
	 */
	public void runMechanism();
	
	/**
	 * Sets data set used for this mechanism.
	 * @param data mechanism data.
	 */
	public void setData(MechanismDataInterface data);
	
	/**
	 * Sets the cut for the ellipsoid method.
	 * @param cut ellipsoid cut.
	 * @see EllipsoidMethod#CUT_CENTRAL
	 * @see EllipsoidMethod#CUT_DEEP
	 * @see EllipsoidMethod#CUT_SHALLOW
	 */
	public void setCut(int cut);
	
	/**
	 * Sets the epsilon threshold for the ellipsoid method.
	 * @param eps threshold.
	 */
	public void setEpsilon(double eps);
	
	/**
	 * Sets the initial radius for the ellipsoid method.
	 * @param radius initial radius.
	 */
	public void setRadius(double radius);
	
	/**
	 * Sets the maximum number of iterations for the ellipsoid method.
	 * @param n maximum iterations.
	 */
	public void setMaxIterations(int n);
	
	/**
	 * Sets whether or not the ellipsoid method should stop as soon as decomposition is feasible.
	 * @param feas true if feasibility of primal will end ellipsoid.
	 */
	public void setStopIfFeasible(boolean feas);
	
	/**
	 * Sets if all integer solutions that are valid due to the packing property
	 * should be added before running the ellipsoid method.
	 * <p>
	 * The method will also add the zero vector which is always a valid solution.
	 * The iteration in which the solution were found will be set to 0.
	 * @param add true if integer solutions should be added manually.
	 */
	public void setManualIntegerSolutions(boolean add);
	
	/**
	 * Sets if the objective value should be used as gamma value for the deep cut.
	 * <p>
	 * The original paper suggests to use 1 as gamma value for objective cuts. This method
	 * can restore the originally proposed behavior for the deep cut
	 * @param deep_gamma true if objective value should be used as gamma.
	 */
	public void setObjectiveAsGamma(boolean deep_gamma);
	
	/**
	 * Set another initial center than the originally proposed one.
	 * <p>
	 * The original paper suggests to use w<sub>i,S</sub>=0 and z=1 as center.
	 * This method allows to change the center by giving a type. The following types
	 * are available:
	 * <ul>
	 * <li>0: original center as defined above
	 * <li>1: vector consisting of 0s
	 * <li>2: vector consisting of 1s
	 * </ul>
	 * @param center_type type of center vector as defined above.
	 */
	public void setInitialCenter(int center_type);
	
	/**
	 * Overwrite the integrality gap defined by the oracle to a custom value.
	 * <p>
	 * By default the primal decomposition is solved using the integrality gap returned
	 * by the separation oracle. This method can overwrite the gap to any value. Setting it
	 * to 0 will use the original gap again.
	 * @param int_gap integrality gap used for decomposition.
	 */
	public void setOverwriteGap(double int_gap);
	
	/**
	 * Set if the decomposition should take the absolute deviation into account.
	 * <p>
	 * This will switch to another primal decomposition class with a modified objective
	 * function which will minimize the absolute deviation from the social welfare.
	 * @param decomp_min_dev set to true to minimize the deviation.
	 */
	public void setMinimizeDeviation(boolean decomp_min_dev);
	
	/**
	 * Set identifier used for file or folder names.
	 * @param id identifier.
	 */
	public void setIdenfitier(String id);
}
