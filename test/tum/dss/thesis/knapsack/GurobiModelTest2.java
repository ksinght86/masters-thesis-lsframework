package tum.dss.thesis.knapsack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GurobiModelTest2 {
	public GurobiModel tester;

	@Before
	public void setUp() throws Exception {
		tester = new GurobiModel(new KnapsackData2());
		tester.solveLP();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetResult() {
		double[][] res = new double[][] {
				{tester.getResult(0, 0), tester.getResult(0, 1)},
				{tester.getResult(1, 0), tester.getResult(1, 1)}
		};
		assertArrayEquals("Wrong result 1", new double[] {0, 0}, res[0], 0);
		assertArrayEquals("Wrong result 2", new double[] {0, 1}, res[1], 0);
	}
	
	@Test
	public void testGetSolUsed() {
		assertEquals("Wrong number of used solutions", 1, tester.getSolUsed().size());
		assertArrayEquals("Wrong sol used", new Integer[]{1}, tester.getSolUsed().get(1).toArray(new Integer[1]));
	}

}
