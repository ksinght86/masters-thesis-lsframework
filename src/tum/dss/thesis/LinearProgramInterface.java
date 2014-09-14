package tum.dss.thesis;

import com.google.common.collect.ListMultimap;

/**
 * Interface for relaxed IP, e.g. a Gurobi Model.
 * @author Florian Stallmann
 *
 */
public interface LinearProgramInterface {
	/**
	 * Injects data to model and reset all variables for a new run.
	 * @param data mechanism data.
	 */
	public void setData(MechanismDataInterface data);
	
	/**
	 * Creates the LP model with relaxation as default.
	 */
	public void createLP();
	
	/**
	 * Creates the LP model either relaxed or integral.
	 * @param relax relaxation of integrality constraint.
	 */
	public void createLP(boolean relax);
	
	/**
	 * Solves LP model (after creation) and map results if necessary.
	 * <p>
	 * If the model hasn't been created before this method should call <code>createLP</code> automatically.
	 */
	public void solveLP();
	
	/**
	 * Retrieves result allocation for player i and subset j.
	 * <p>
	 * The <code>solveLP</code> method must be called before!
	 * @param i index of player.
	 * @param j index of subset.
	 * @return  LP solution for pair (i,j).
	 */
	public double getResult(int i, int j);
	
	/**
	 * Retrieves objective value respectively the optimal social welfare of the LP.
	 * <p>
	 * The <code>solveLP</code> method must be called before!
	 * @return objective value.
	 */
	public double getObjective();
	
	/**
	 * Retrieves map of indices (i,j) where x<sub>i,j</sub> &gt; 0, so solutions that have been used.
	 * <p>
	 * The <code>solveLP</code> method must be called before!
	 * <p>
	 * <code>Multimap</code> will save multiple values for one key, e.g. 1{@literal ->}[2,3];2{@literal ->}[1,5,6].
	 * @return map of all indices of used solutions.
	 */
	public ListMultimap<Integer, Integer> getSolUsed();
}
