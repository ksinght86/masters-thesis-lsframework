package tum.dss.thesis.knapsack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GreedyAlgorithmTest1 {
	public GreedyAlgorithm tester;

	@Before
	public void setUp() {
		tester = new GreedyAlgorithm(new KnapsackData1());
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCalcV() {
		assertEquals("0 expected, 80-80", 0.0, tester.calcV(0, 0, 1), 0.0);
		assertEquals("20 expected, 100-80", 20.0, tester.calcV(0, 1, 1), 0.0);
		assertEquals("140 expected, 200-80", 120.0, tester.calcV(0, 3, 1), 0.0);
	}

	@Test
	public void testStartAlgorithm() {
		tester.startAlgorithm();
//		System.out.println(tester);
		assertArrayEquals("Unexpected greedy result", new int[] {3, 1, 0}, tester.q);

		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result (repetition 1)", new int[] {3, 1, 0}, tester.q);

		tester.startAlgorithm();
		assertArrayEquals("Unexpected greedy result (repetition 2)", new int[] {3, 1, 0}, tester.q);
	}
}
