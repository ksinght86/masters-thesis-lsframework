package tum.dss.thesis;

/**
 * Interface for the data for the mechanism. It stores an array with the players and the itemset class.
 * @see ItemsetHandlerInterface
 * @see PlayerInterface
 * @author Florian Stallmann
 *
 */
public interface MechanismDataInterface {
	/**
	 * Gets the number of subsets in the itemset.
	 * <p>
	 * This method will probably call the {@link ItemsetHandlerInterface#getNumSubsets()} method of the itemset.
	 * @return number of subsets.
	 */
	public int getNumSubsets();
	
	/**
	 * Gets the number of items in a specific set.
	 * <p>
	 * This method will probably pass the parameter to the {@link ItemsetHandlerInterface#getNumItemsInSubset(int)} method of the itemset.
	 * @param j index of set.
	 * @return  number of items in this set.
	 */
	public int getNumItemsInSubset(int j);
	
	/**
	 * Gets the item set for this mechanism data.
	 * @return item set.
	 */
	public ItemsetHandlerInterface getItemsetHandler();
	
	/**
	 * Sets the itemset for this mechanism data.
	 * @param itemset itemset instance.
	 */
	public void setItemsetHandler(ItemsetHandlerInterface itemset);
	
	/**
	 * Gets the number of players in this dataset.
	 * @return number of players.
	 */
	public int getNumPlayers();
	
	/**
	 * Adds new player to the mechanism data.
	 * @param player player to add.
	 */
	public void addPlayer(PlayerInterface player);
	
	/**
	 * Gets the player by index.
	 * @param i index of player.
	 * @return  player.
	 */
	public PlayerInterface getPlayer(int i);
	
	/**
	 * Gets the valuation of player with given index and set.
	 * <p>
	 * This method will probably pass the parameter to the {@link PlayerInterface#getValuation(int)} method of the corresponding player.
	 * @param i index of player.
	 * @param j index of set.
	 * @return  valuation.
	 */
	public double getValuation(int i, int j);
}
