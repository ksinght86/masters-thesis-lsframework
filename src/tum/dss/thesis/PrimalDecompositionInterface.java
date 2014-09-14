package tum.dss.thesis;

import java.util.List;

import tum.dss.thesis.lsframework.AbstractMechanism;

/**
 * Interface for the primal LP of the convex decomposition model, e.g. a Gurobi model.
 * @author Florian Stallmann
 *
 */
public interface PrimalDecompositionInterface {
	/**
	 * Creates or update the LP model.
	 */
	public void createLP();
	
	/**
	 * Set the integrality gap of the oracle or any other value.
	 * @param integrality_gap integrality gap of the separation oracle.
	 */
	public void setIntegralityGap(double integrality_gap);
	
	/**
	 * Set the maximum social welfare of the LP relaxation. This is needed for validation
	 * or for calculating the deviation.
	 * @param welfare optimal social welfare.
	 */
	public void setMaxSocialWelfare(double welfare);
	
	/**
	 * Solves the LP model.
	 * <p>
	 * If the model hasn't been created before this method should call <code>createLP</code> automatically.
	 */
	public void solveLP();
	
	/**
	 * Test if LP model is already feasible.
	 * <p>
	 * The <code>createLP</code> needs to be called before to update the current model.
	 * Otherwise (without model) the method will always return false.
	 * @return true if feasible, otherwise false.
	 */
	public boolean testFeasibility();
	
	/**
	 * Gets the result matrix as double array.
	 * <p>
	 * The <code>solveLP</code> method must be called before!
	 * @return convex decomposition solution.
	 */
	public double[] getResult();
	
	/**
	 * Retrieves objective value of the LP.
	 * <p>
	 * The <code>solveLP</code> method must be called before!
	 * This value should always be 1, otherwise something went wrong.
	 * @return objective value.
	 */
	public double getObjective();
	
	/**
	 * Returns the calculated expected social welfare.
	 * <p>
	 * The <code>solveLP</code> method must be called before!
	 * <p>
	 * The return value should be equal to the optimal social welfare divided by the integrality gap
	 * of the separation oracle. A validation of this is performed in the {@link AbstractMechanism#step3SolvePrimal()} method.
	 * @return social welfare.
	 */
	public double getExpectedSocialWelfare();
	
	/**
	 * Get list of indices of all integer solutions where lambda &gt; 0.
	 * <p>
	 * The <code>solveLP</code> method must be called before!
	 * @return index of used solutions.
	 */
	public List<Integer> getSolUsed();
}
