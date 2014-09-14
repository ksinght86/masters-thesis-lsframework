package tum.dss.thesis;

import tum.dss.thesis.lsframework.EllipsoidMethodData;

/**
 * Interface for an individual player in the mechanism data. It stores or calculates the valuations.
 * @author Florian Stallmann
 *
 */
public interface PlayerInterface {
	/**
	 * Gets the valuation for given subset index (use oracle if necessary).
	 * @param j index of set.
	 * @return  valuation.
	 */
	public double getValuation(int j);
	
	/**
	 * Sets the valuation for all subsets (override existing).
	 * <p>
	 * The index of the array must equal the index of the subset. The array has to be monotone as this method
	 * will not check for monotonicity.
	 * @param v valuation as double array.
	 */
	public void setValuations(double[] v);
	
	/**
	 * Sets a single valuation for given subset index (takes care of monotonicity automatically).
	 * <p>
	 * This method is the implementation of the first part of Claim 3.3. It will monotonize
	 * the valuation of this player for all supersets. If a subset has a higher valuation this
	 * this update will be skipped. Hence, it is not possible to decrease a valuation if a
	 * subset with a higher valuation already exists. It is important that
	 * each time the ellipsoid method updates all player valuation the <code>resetValuation</code>
	 * method is called before. This happens in {@link EllipsoidMethodData#updatePlayerValuation(org.jscience.mathematics.vector.Vector)}.
	 * @param j index of set.
	 * @param v valuation.
	 */
	public void setValuation(int j, double v);
	
	/**
	 * Method to remove all given valuations.
	 */
	public void dropOriginalValuations();
	
	/**
	 * Method to set all valuations to zero.
	 */
	public void resetValuations();
	
	/**
	 * Injects itemset which is needed to calculate the valuations of subsets.
	 * @param itemset itemset of underlying mechanism data.
	 */
	public void setItemsetHandler(ItemsetHandlerInterface itemset);
	
	/**
	 * Gets the name of this player.
	 * @return player name.
	 */
	public String getName();
}
