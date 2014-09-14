package tum.dss.thesis.lsframework;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntegerSolutionTest {
	public IntegerSolution tester;

	@Before
	public void setUp() throws Exception {
		tester = new IntegerSolution(1, new int[] {1, 0, 1, 1});
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalculateWelfare() {
		tester.calculateWelfare(new double[] {2.0, 3.0, 1.0, 4.0});
		assertEquals("Wrong welfare", 7.0, tester.getWelfare(), 0);
	}

}
