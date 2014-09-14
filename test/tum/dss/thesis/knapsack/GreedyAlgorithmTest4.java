package tum.dss.thesis.knapsack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GreedyAlgorithmTest4 {
	public GreedyAlgorithm tester;

	@Before
	public void setUp() {
		tester = new GreedyAlgorithm(new KnapsackData4());
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testStartAlgorithm() {
		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result", new int[] {1, 1, 1}, tester.q);
		
		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result (repetition 1)", new int[] {1, 1, 1}, tester.q);
		
		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result (repetition 2)", new int[] {1, 1, 1}, tester.q);
		
		System.out.println(tester);
	}
}
