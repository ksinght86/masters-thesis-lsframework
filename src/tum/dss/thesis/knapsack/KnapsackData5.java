package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Knapsack test data 5.
 * @author Florian Stallmann
 *
 */
@SuppressWarnings("serial")
public class KnapsackData5 extends AbstractMechanismData {
	public KnapsackData5() {
		itemset_handler = new ItemsetHandler(5);
		
		//Bidder 1
		addPlayer(new Player("Player 1", new double[]{0, 20, 20, 20, 20, 20}) );
		//Bidder 2
		addPlayer(new Player("Player 2", new double[]{0, 50, 50, 50, 50, 50}) );
		//Bidder 3
		addPlayer(new Player("Player 3", new double[]{0, 40, 40, 40, 40, 40}) );
	}

}
