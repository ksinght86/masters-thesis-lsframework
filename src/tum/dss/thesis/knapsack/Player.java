package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractPlayer;

/**
 * A single knapsack player.
 * @author Florian Stallmann
 *
 */
public class Player extends AbstractPlayer {

	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = -5442293064572754337L;

	/**
	 * Initializes the player with name and valuation vector.
	 * @param name      name of player.
	 * @param valuation double array with valuations.
	 */
	public Player(final String name, final double[] valuation) {
		super(name, valuation);
	}
	
	/**
	 * Initializes the player with name and number of items. All valuations are 0 in this case.
	 * @param name  name of player.
	 * @param items number of items respectively valuations.
	 */
	public Player(final String name, final int items) {
		super(name, items);
	}

}
