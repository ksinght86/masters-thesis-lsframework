package tum.dss.thesis.knapsack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GurobiModelTest3 {
	public GurobiModel tester;

	@Before
	public void setUp() throws Exception {
		tester = new GurobiModel(new KnapsackData6());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetResult() {
		tester.solveLP();
		double[][] res = tester.x_double;
		assertArrayEquals("Wrong result 1", new double[] {0, 1, 0, 0, 0}, res[0], 0);
		assertArrayEquals("Wrong result 2", new double[] {0, 0, 0.5, 0, 0.5}, res[1], 0);
		assertArrayEquals("Wrong result 3", new double[] {0, 0, 0, 0, 0}, res[2], 0);
	}
	
	@Test
	public void testGetSolUsed() {
		tester.solveLP();
		assertEquals("Wrong number of used solutions", 3, tester.getSolUsed().size());
		assertArrayEquals("Wrong sol used 1", new Integer[]{1}, tester.getSolUsed().get(0).toArray(new Integer[1]));
		assertArrayEquals("Wrong sol used 2", new Integer[]{2,4}, tester.getSolUsed().get(1).toArray(new Integer[1]));
	}
	
	@Test
	public void testGetObjective() {
		tester.solveLP();
		assertEquals("Wrong objective value", 11, tester.getObjective(), 0);
	}
	
	@Test
	public void testUnrelaxedLP() {
		tester.createLP(false);
		tester.solveLP();

		assertEquals("Wrong unrelaxed objective value", 10, tester.getObjective(), 0);
		
		double[][] res = tester.x_double;
		assertArrayEquals("Wrong unrelaxed result 1", new double[] {0, 1, 0, 0, 0}, res[0], 0);
		assertArrayEquals("Wrong unrelaxed result 2", new double[] {0, 0, 1, 0, 0}, res[1], 0);
		assertArrayEquals("Wrong unrelaxed result 3", new double[] {0, 0, 0, 0, 0}, res[2], 0);
	}

}
