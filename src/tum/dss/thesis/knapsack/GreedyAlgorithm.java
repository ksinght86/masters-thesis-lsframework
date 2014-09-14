package tum.dss.thesis.knapsack;

import java.util.Arrays;
import java.util.logging.Logger;

import tum.dss.thesis.MechanismDataInterface;

/**
 * Simple greedy algorithm to solve multi-unit auctions with guaranteed integrality gap of 2.
 * @author Florian Stallmann
 *
 */
public class GreedyAlgorithm {
	private static final Logger log = Logger.getLogger( GreedyAlgorithm.class.getName() );
	
	/** Mechanism data to run greedy on */
	protected final MechanismDataInterface data;
	
	/** Calculated quantities, contains result after running greedy */
	protected int[] q;
	
	/**
	 * Initializes the algorithm with data.
	 * @param data mechanism data.
	 */
	public GreedyAlgorithm(final MechanismDataInterface data) {
		this.data = data;
		q = new int[data.getNumPlayers()];
	}

	
	/**
	 * Starts greedy algorithm to find the allocation with value at least OPT/2.
	 * @return integer solution.
	 */
	public int[] startAlgorithm() {
		log.finer("Greedy algorithm started.");
		
		// (1)
		int i_star, j_star, M;
		double v_max;
		
		//Initialize M and q
		M = data.getNumSubsets();
		Arrays.fill(q, 0); //necessary because greedy is called multiple times

		//While M>0 repeatedly do
		while(M > 0) {
			// (a) find i*, j* that maximizes v(j|q)/j
			// Implicit tie breaker (i.e. all valuations are 0): choose smallest i
			// TODO better way to find i* and j* ??? (e.g. by ordered data structure which stores marginal values)
			i_star = 0;
			j_star = 0;
			v_max = 0;
			for (int i = 0; i < data.getNumPlayers(); i++) {
				for (int j = 1; j <= data.getNumSubsets(); j++) { //important: iterate all items (not just M, is j* too high M gets decreased anyway
					//Check whether this is valid iteration (invalid means that assigning j items to i would exceed number of items)
					if((j + q[i]) > data.getNumSubsets() ) {
						continue;
					}
					//Calculate marginal value per unit and compare
					double tmp_v = calcV(i, j, q[i]);
					if((tmp_v / j) > v_max) {
						v_max = tmp_v;
						i_star = i;
						j_star = j;
						log.finer(String.format("New v_max=%f for i*=%d and j*=%d found!", v_max, i, j));
					}
				}
			}
			//No (i_star, j_star) pair found?! -> prevent infinite loop
			if(j_star == 0) {
				break; //stop loop, no more allocations possible here, go to step 2
				//throw new RuntimeException("Infinite loop: Greedy cannot find a solution.");
			}
			// (b) if j*<=M set q (allocate j* more items to i*)
			if(j_star <= M) {
				q[i_star] = q[i_star] + j_star;
			}
			// (c) decrement M by j*
			M = M - j_star;
		}
		
		// (2)
		double Greedy = 0, Max = 0;
		int i_max = 0;
		
		//Get Greedy and Max valuation
		for (int i = 0; i < data.getNumPlayers(); i++) {
			//Sum of above allocation
			Greedy = Greedy + data.getValuation(i, q[i]);
			
			//Highest valuation among players
			if( data.getValuation(i, data.getNumSubsets()) > Max ) {
				i_max = i;
				Max = data.getValuation(i, data.getNumSubsets());
			}
		}
		
		//If one player dominates found allocation
		if(Max > Greedy) {
			log.fine(String.format("In Max (%f) > Greedy (%f) case!\n", Max, Greedy));
			//Reset all players
			for (int i = 0; i < data.getNumPlayers(); i++) {
				q[i] = 0;
			}
			//Assign all to i_max
			q[i_max] = data.getNumSubsets();
		}
		
		return q;
	}
	
	/**
	 * Calculates marginal value v<sub>i</sub>(x|j).
	 * @param i player index.
	 * @param x for any x {@literal >=} 0.
	 * @param j given quantity j.
	 * @return  v<sub>i</sub>(x|j) = v<sub>i</sub>(x+j) - v<sub>i</sub>(j).
	 * @throws IllegalArgumentException subsets are not valid
	 */
	public double calcV(final int i, final int x, final int j) {
		if(x < 0) throw new IllegalArgumentException("CalcV: x cannot be lower than 0");
		if(j < 0) throw new IllegalArgumentException("CalcV: j cannot be lower than 0");
		return data.getValuation(i, x+j) - (j>0 ? data.getValuation(i, j) : 0);
	}
	
	@Override
	public String toString() {
		String s = "Greedy solution:\n";
		double v = 0;
		for (int i = 0; i < data.getNumPlayers(); i++) {
			s += data.getPlayer(i).getName() + " gets " + q[i] + " items\n";
			v += data.getValuation(i, q[i]);
		}
		s += "Objective value: " + v;
		return s;
	}
}
