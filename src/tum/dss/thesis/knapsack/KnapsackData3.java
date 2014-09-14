package tum.dss.thesis.knapsack;

import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Knapsack test data 3.
 * @author Florian Stallmann
 *
 */
@SuppressWarnings("serial")
public class KnapsackData3 extends AbstractMechanismData {
	public KnapsackData3() {
		itemset_handler = new ItemsetHandler(5);
		
		//Bidder 1
		addPlayer(new Player("Player 1", new double[]{0, 110, 115, 120, 125, 130}) );
		//Bidder 2
		addPlayer(new Player("Player 2", new double[]{0, 30, 60, 90, 120, 150}) );
		//Bidder 3
		addPlayer(new Player("Player 3", new double[]{0, 40, 50, 60, 70, 80}) );
	}
	
//	public int demandOracle(int i, int p) {
//		double max;
//		int j_max = 1;
//		max = getValuation(i, 1) - p * 1;
//		for (int j = 1; j <= getNumItems(); j++) {
//			if((getValuation(i, j) - p * j) > max) {
//				max = getValuation(i, j) - p * j;
//				j_max = j;
//			}
//		}
//		return j_max;
//	}

}
