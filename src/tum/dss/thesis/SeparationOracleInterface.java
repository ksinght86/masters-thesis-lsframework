package tum.dss.thesis;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.Vector;

import tum.dss.thesis.lsframework.EllipsoidMethodData;

/**
 * Interface for the separation oracle.
 * @author Florian Stallmann
 *
 */
public interface SeparationOracleInterface {
	/**
	 * Dependency injection: sets data and initializes variables.
	 * @param data special data for ellipsoid method.
	 */
	public void setData(EllipsoidMethodData data);
	
	/**
	 * Ask oracle whether given point (valuation vector) is in polytope.
	 * <p>
	 * This method will call the approximation algorithm and add new integer solutions via {@link EllipsoidMethodData#addIntegerSolution(int[], int)}.
	 * It will also perform all the mapping needed to call the algorithm. To retrieve the correct cut the ellipsoid method
	 * will either call <code>getConstraintCut</code> or <code>getObjectiveCut</code>.
	 * @param wz valuation vector.
	 * @return   true if point in polytope, false otherwise.
	 */
	public boolean ask(Vector<FloatingPoint> wz);
	
	/**
	 * Gets the constraint cut as vector.
	 * @return constraint cut.
	 */
	public Vector<FloatingPoint> getConstraintCut();
	
	/**
	 * Gets the objective cut as vector.
	 * @return objective cut.
	 */
	public Vector<FloatingPoint> getObjectiveCut();
	
	/**
	 * Gets the gamma value for the deep cut.
	 * <p>
	 * Note that this method might be obsolete as the LS framework always uses the gamma value 1.
	 * However, the method has been kept to make it easy to adjust this value for future experiments.
	 * For example, using the objective value or an arbitrary value other than 1 might improve the runtime.
	 * @return gamma.
	 */
	public FloatingPoint getGammaValue();
	
	/**
	 * This method returns true if the vector of wz that was used for the last call
	 * of the <code>ask</code> method was already asked before.
	 * @return true if wz is a duplicate, otherwise false.
	 */
	public boolean lastWzWasAskedBefore();
	
	/**
	 * This method returns true if the vector wz that was used for the last call
	 * of the <code>ask</code> method lead to a new integer solution.
	 * @return true if new solution was found and it is not a duplicate, otherwise false.
	 */
	public boolean lastWzWasNewSolution();
	
	/**
	 * Methods which is called to retrieve integer solution.
	 * <p>
	 * This method needs to be implemented for the specific mechanism, e.g. a greedy algorithm for the knapsack problem.
	 * @return integer array with integer solution.
	 */
	public int[] retrieveIntegerSolution();
	
	/**
	 * Gets the integrality gap of the oracle respectively the approximation algorithm.
	 * @return integrality gap alpha.
	 */
	public double getIntegralityGap();
	
	/**
	 * Sets if the objective value should be used as gamma value for the deep cut.
	 * @param deep_gamma true if objective value should be used as gamma.
	 */
	public void setObjectiveAsGamma(boolean deep_gamma);
}
