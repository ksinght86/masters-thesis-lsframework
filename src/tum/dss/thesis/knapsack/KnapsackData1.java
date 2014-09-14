package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Knapsack test data 1.
 * @author Florian Stallmann
 *
 */
@SuppressWarnings("serial")
public class KnapsackData1 extends AbstractMechanismData {
	public KnapsackData1() {
		itemset_handler = new ItemsetHandler(4);
		
		//Bidder 1
		addPlayer(new Player("Player 1", new double[]{0, 80, 100, 180, 200}) );
		//Bidder 2
		addPlayer(new Player("Player 2", new double[]{0, 90, 120, 120, 120}) );
		//Bidder 3
		addPlayer(new Player("Player 3", new double[]{0, 10, 20, 60, 80}) );
	}

}
