package tum.dss.thesis.lsframework;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tum.dss.thesis.knapsack.KnapsackData1;

public class PrimalDecompositionTest {
	public PrimalDecomposition tester;
	public EllipsoidMethodData tester_data;

	@Before
	public void setUp() throws Exception {
		//Gurobi result test data (3 players and 4 items)
		double[][] testdata = new double[][]{
				{0, 0, 0.5, 0, 0},
				{0, 1, 0, 0.5, 0},
				{0, 0, 0, 0, 0}
		};
		tester_data = new EllipsoidMethodData(new KnapsackData1()); //valuations only important for expected social welfare
		tester_data.importLPResults(new LinearProgramMock(testdata));
		tester_data.addIntegerSolution(new int[]{1, 0, 0}, 1);
		tester_data.addIntegerSolution(new int[]{0, 1, 0}, 2);
		//x_star      = {0.5, 1, 0.5}
		//x_valuation = {100, 90, 120}
		//by default only 2 integer solutions, so model is infeasible!
		tester = new PrimalDecomposition(tester_data);
		tester.setIntegralityGap(2.0);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTestFeasibility() {
		assertFalse("Without LP the primal should not be feasible", tester.testFeasibility());
		
		tester.createLP();
		assertFalse("Primal should not be feasible", tester.testFeasibility());

		tester_data.addIntegerSolution(new int[]{0, 0, 0}, 3);
		tester.createLP();
		assertFalse("Primal should not be feasible still", tester.testFeasibility());

		tester_data.addIntegerSolution(new int[]{0, 0, 1}, 4);
		tester.createLP();
		assertTrue("Primal should be feasible now", tester.testFeasibility());

		tester_data.addIntegerSolution(new int[]{1, 1, 1}, 5);
		tester.createLP();
		assertTrue("Primal should be feasible still", tester.testFeasibility());
	}

	@Test
	public void testGetResult() {
		tester_data.addIntegerSolution(new int[]{1, 1, 1}, 3);
		tester_data.addIntegerSolution(new int[]{0, 0, 1}, 4);
		tester.solveLP(2);
		
		assertArrayEquals("Wrong result", new double[] {0.25, 0.5, 0, 0.25}, tester.getResult(), 0);
	}

	@Test
	public void testGetSolUsed() {
		tester_data.addIntegerSolution(new int[]{1, 1, 1}, 3);
		tester_data.addIntegerSolution(new int[]{0, 0, 1}, 4);
		tester.solveLP(2);

		assertEquals("Wrong number of used solutions", 3, tester.getSolUsed().size());
		assertEquals("Wrong sol used 1", new Integer(0), tester.getSolUsed().get(0));
		assertEquals("Wrong sol used 2", new Integer(1), tester.getSolUsed().get(1));
		assertEquals("Wrong sol used 3", new Integer(3), tester.getSolUsed().get(2));
	}
	
	@Test
	public void testGetExpectedSocialWelfare() {
		tester_data.addIntegerSolution(new int[]{0, 0, 1}, 3);
		tester.solveLP(2);
		assertEquals("Wrong expected social welfare", 100, tester.getExpectedSocialWelfare(), 0);
	}

//	@Test
//	public void testUniqueness() {
//		//In this test I tried to find out if the result of the decomposition depends on
//		//the order in which integer solutions are added. Result: Yes, the order matters.
//		//For future work it would be interesting to adjust the decomposition so that
//		//solutions of "better quality" (to be defined) are preferred.
//		tester_data.addIntegerSolution(new int[]{0, 0, 0}, 3);
//		tester_data.addIntegerSolution(new int[]{0, 0, 1}, 4);
//		tester_data.addIntegerSolution(new int[]{1, 0, 1}, 5);
//		tester_data.addIntegerSolution(new int[]{1, 1, 0}, 5);
//		tester_data.addIntegerSolution(new int[]{0, 1, 1}, 5);
//		
//		tester.createLP(2);
//		tester.solveLP();
//		System.out.println(tester);
//	}
}
