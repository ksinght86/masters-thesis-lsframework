package tum.dss.thesis;

/**
 * Interface for sets and subsets of items. This class has to handle all set operations.
 * <p>
 * Example data used in following descriptions:
 * <ul>
 * <li>Items: A, B, C
 * <li>Quantities: A=2, B=1, C=2
 * <li>Subsets: {{A,A},{A,B,C},{C,C},{A}}
 * </ul>
 * 
 * @author Florian Stallmann
 * 
 */
public interface ItemsetHandlerInterface {
	/**
	 * Checks if set with index k is a subset of set with index j.
	 * <ul>
	 * <li>e.g. k=3, j=0: true as {A} is subset of {A,A}
	 * <li>e.g. k=3, j=1: true as {A} is subset of {A,B,C}
	 * <li>e.g. k=3, j=3: false as {A,A} is not a real subset of {A,A}
	 * </ul>
	 * @param k index of subset.
	 * @param j index of superset.
	 * @return  true if k &sub; j, false otherwise.
	 */
	public boolean isSubset(int k, int j);
	
	/**
	 * Checks if set with index k is a superset of set with index j.
	 * <ul>
	 * <li>e.g. k=0, j=3: true as {A,A} is superset of {A}
	 * <li>e.g. k=1, j=3: true as {A,B,C} is superset of {A}
	 * <li>e.g. k=3, j=3: false as {A,A} is not a real superset of {A,A}
	 * </ul>
	 * @param k index of superset.
	 * @param j index of subset.
	 * @return  true if k &sup; j, false otherwise.
	 */
	public boolean isSuperset(int k, int j);
	
	/**
	 * Gets string representation of set j.
	 * @param j index of set.
	 * @return  string with elements of the set.
	 */
	public String getSet(int j);
	
	/**
	 * Gets the number of items in subset with index j.
	 * <ul>
	 * <li>e.g. j=1: {A,B,C} has 3 items
	 * </ul>
	 * @param j index of set.
	 * @return  number of items in set j.
	 */
	public int getNumItemsInSubset(int j);
	
	/**
	 * Gets the number of distinct items in this itemset.
	 * <ul>
	 * <li>e.g. A,B,C = 3 distinct items
	 * </ul>
	 * @return number of distinct items.
	 */
	public int getNumDistinctItems();
	
	/**
	 * Gets the number of subsets in the whole itemset.
	 * <ul>
	 * <li>e.g. here we have 4 subsets
	 * </ul>
	 * @return number of subsets.
	 */
	public int getNumSubsets();
	
	/**
	 * Gets the number of items of given type.
	 * <ul>
	 * <li>e.g. type=0: 2 items of A available
	 * </ul>
	 * @param type index of item type.
	 * @return     number of items of type.
	 */
	public int getNumItems(int type);
	
	/**
	 * Gets the total number of items of all types.
	 * <ul>
	 * <li>e.g. A+B+C = 5 items given
	 * </ul>
	 * @return number of items of all types.
	 */
	public int getNumItems();
}
