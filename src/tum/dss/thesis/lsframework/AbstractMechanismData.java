package tum.dss.thesis.lsframework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tum.dss.thesis.ItemsetHandlerInterface;
import tum.dss.thesis.MechanismDataInterface;
import tum.dss.thesis.PlayerInterface;

/**
 * Abstract mechanism data which handles basic player and item operations.
 * @author Florian Stallmann
 *
 */
public abstract class AbstractMechanismData implements Serializable, MechanismDataInterface {
	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = 2684532512604741873L;
	
	/** Set of all items and subsets (public because of data reader!) */
	public ItemsetHandlerInterface itemset_handler;
	
	/** List of players (public because of data reader!) */
	public List<PlayerInterface> players = new ArrayList<PlayerInterface>(1000);
	
	@Override
	public int getNumPlayers() {
		return players.size();
	}
	
	@Override
	public PlayerInterface getPlayer(final int i) {
		return players.get(i);
	}
	
	@Override
	public void addPlayer(final PlayerInterface player) {
		player.setItemsetHandler(itemset_handler);
		players.add(player);
	}
	
	@Override
	public double getValuation(final int i, final int j) {
		return getPlayer(i).getValuation(j);
	}
	
	@Override
	public int getNumSubsets() {
		return itemset_handler.getNumSubsets();
	}

	@Override
	public int getNumItemsInSubset(final int j) {
		return itemset_handler.getNumItemsInSubset(j);
	}

	@Override
	public ItemsetHandlerInterface getItemsetHandler() {
		return itemset_handler;
	}

	@Override
	public void setItemsetHandler(final ItemsetHandlerInterface itemset) {
		this.itemset_handler = itemset;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += String.format("Mechanism data with %d players and %d subsets:", getNumPlayers(), getNumSubsets() );
		//Only output 100 players (equally distributed) due to performance
		int inc = Math.max(1, (players.size() / 100) );
		for (int i = 0; i < players.size(); i+=inc) {
			s += "\n" + getPlayer(i).toString();
		}
		if(inc > 1) {
			s += "\n(Max. 100 players are printed due to performance!)";
		}
		return s;
	}
}
