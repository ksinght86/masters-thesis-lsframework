package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Knapsack test data 4.
 * @author Florian Stallmann
 *
 */
@SuppressWarnings("serial")
public class KnapsackData4 extends AbstractMechanismData {
	public KnapsackData4() {
		itemset_handler = new ItemsetHandler(5);
		
		//Bidder 1
		addPlayer(new Player("Player 1", new double[]{0, 50, 60, 70, 80, 90}) );
		//Bidder 2
		addPlayer(new Player("Player 2", new double[]{0, 30, 50, 70, 90, 120}) );
		//Bidder 3
		addPlayer(new Player("Player 3", new double[]{0, 40, 50, 60, 70, 80}) );
	}

}
