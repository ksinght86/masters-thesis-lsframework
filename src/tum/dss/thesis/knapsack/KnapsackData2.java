package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Knapsack test data 2.
 * @author Florian Stallmann
 *
 */
@SuppressWarnings("serial")
public class KnapsackData2 extends AbstractMechanismData {
	public KnapsackData2() {
		itemset_handler = new ItemsetHandler(1);
		
		//Bidder 1
		addPlayer(new Player("Player 1", new double[]{0, 10}) );
		
		//Bidder 2
		addPlayer(new Player("Player 2", new double[]{0, 20}) );
	}
}
