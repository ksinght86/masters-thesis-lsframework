package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractItemsetHandler;

/**
 * Handles single kind of items for the multi-unit auction / knapsack problem.
 * The following is equivalent: index = quantity = |subsets|.
 * @author Florian Stallmann
 *
 */
public class ItemsetHandler extends AbstractItemsetHandler {
	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = 3450629420097005326L;
	
	/** Number of items */
	protected final int number;
	
	/**
	 * Initializes with number of items.
	 * @param items available quantity of items.
	 */
	public ItemsetHandler(final int items) {
		number = items;
	}

	@Override
	public int getNumItemsInSubset(int j) {
		return (j+1); //Number of items, j+1 as "0" is explicitly modeled as separate item
	}

	@Override
	public int getNumDistinctItems() {
		return 1; //Only one type of item
	}

	@Override
	public int getNumSubsets() {
		return number; //Each quantity seen as subset
	}

	@Override
	public int getNumItems(int type) {
		return number; //only one type of item
	}

	@Override
	public int getNumItems() {
		return number;
	}

	@Override
	public boolean isSubset(int k, int j) {
		return (k < j); //Every quantity k smaller than j is a subset
	}

	@Override
	public boolean isSuperset(int k, int j) {
		return (k > j); //Every quantity k greater than j is a superset
	}

	@Override
	public String getSet(int j) {
		return Integer.toString(j); //Index of set = Quantity
	}

}
