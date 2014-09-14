package tum.dss.thesis.knapsack;

import java.util.logging.Logger;

import tum.dss.thesis.lsframework.AbstractSeparationOracle;
import tum.dss.thesis.lsframework.EllipsoidMethodData;

/**
 * Separation Oracle for the knapsack problem, uses Greedy to find integer solution.
 * @author Florian Stallmann
 *
 */
public class SeparationOracle extends AbstractSeparationOracle {
	private static final Logger log = Logger.getLogger( SeparationOracle.class.getName() );
	
	/** Greedy algorithm to solve knapsack problem with guaranteed integrality gap */
	protected GreedyAlgorithm algorithm;
	
	/**
	 * Initializes without data.
	 */
	public SeparationOracle() {
		super();
	}
	
	/**
	 * Initializes with ellipsoid compatible data and feed instance of greedy with data.
	 * @param data ellipsoid method data.
	 */
	public SeparationOracle(final EllipsoidMethodData data) {
		super(data);
	}
	
	@Override
	public void setData(final EllipsoidMethodData data) {
		super.setData(data);
		algorithm = new GreedyAlgorithm(data);
	}

	/**
	 * Starts greedy to find integer solution (translation handled by abstract oracle).
	 * @return integer solution returned by greedy.
	 */
	public int[] retrieveIntegerSolution() {
		//Get Greedy solution
		int[] sol = algorithm.startAlgorithm();
		
		//DEBUG: output greedy solution
		log.finer(algorithm.toString());
		
		return sol;
	}

	@Override
	public double getIntegralityGap() {
		return 2; //integrality gap of given greedy = 2
	}

}
