package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Knapsack test data 6.
 * @author Florian Stallmann
 *
 */
@SuppressWarnings("serial")
public class KnapsackData6 extends AbstractMechanismData {
	public KnapsackData6() {
		itemset_handler = new ItemsetHandler(4);
		
		//Bidder 1
		addPlayer(new Player("Player 1", new double[]{0, 6, 6, 6, 6}) );
		//Bidder 2
		addPlayer(new Player("Player 2", new double[]{0, 2, 4, 4, 6}) );
		//Bidder 3
		addPlayer(new Player("Player 3", new double[]{0, 0, 0, 1, 1}) );
	}

}
