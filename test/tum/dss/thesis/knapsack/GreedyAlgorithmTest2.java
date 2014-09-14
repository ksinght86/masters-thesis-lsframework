package tum.dss.thesis.knapsack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GreedyAlgorithmTest2 {
	public GreedyAlgorithm tester;

	@Before
	public void setUp() {
		tester = new GreedyAlgorithm(new KnapsackData2());
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCalcV() {
		assertEquals("0 expected, 10-10", 0.0, tester.calcV(0, 0, 1), 0.0);
//		assertEquals("0 expected, x+j too high", Double.NEGATIVE_INFINITY, tester.calcV(0, 1, 1), 0.0);
		assertEquals("0 expected, 20-20", 0.0, tester.calcV(1, 0, 1), 0.0);
//		assertEquals("0 expected, x+j too high", Double.NEGATIVE_INFINITY, tester.calcV(1, 1, 1), 0.0);
	}

	@Test
	public void testStartAlgorithm() {
		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result", new int[] {0, 1}, tester.q);
		
		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result (repetition 1)", new int[] {0, 1}, tester.q);
		
		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result (repetition 2)", new int[] {0, 1}, tester.q);
		
		System.out.println(tester);
	}
}
