package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Knapsack test data 7.
 * @author Florian Stallmann
 *
 */
@SuppressWarnings("serial")
public class KnapsackData7 extends AbstractMechanismData {
	public KnapsackData7() {
		itemset_handler = new ItemsetHandler(10);
		
		//Bidder 1
		addPlayer(new Player("Player 1", new double[]{0, 0, 20, 30, 40, 40, 40, 40, 40, 40, 40}) );
		//Bidder 2
		addPlayer(new Player("Player 2", new double[]{0, 0,  0, 45, 60, 70, 80, 90, 90, 90, 90}) );
		//Bidder 3
		addPlayer(new Player("Player 3", new double[]{0, 0,  0,  0,  0, 100, 120, 140, 160, 175, 190}) );
	}

}
