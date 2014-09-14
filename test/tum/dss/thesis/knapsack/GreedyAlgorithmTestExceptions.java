package tum.dss.thesis.knapsack;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GreedyAlgorithmTestExceptions {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	//Test object
	public GreedyAlgorithm tester;

	@Before
	public void setUp() {
		//Data set with only 2 players and 1 item
		tester = new GreedyAlgorithm(new KnapsackData2());
	}

	@After
	public void tearDown() {
	}
	
//	@Test
//	public void testCalcVHighValues() {
//		assertEquals("0 expected, j too high", 0.0, tester.calcV(0, 0, 2), 0.0);
//		assertEquals("0 expected, x too high", 0.0, tester.calcV(0, 2, 0), 0.0);
//	}

	@Test
	public void testCalcVExcX() {
		//x must be at least 0
		exception.expect(IllegalArgumentException.class);
		tester.calcV(0, -1, 0);
	}
	
	@Test
	public void testCalcVExcJ() {
		//j must be at least 1
		exception.expect(IllegalArgumentException.class);
		tester.calcV(0, 0, -1);
	}
}
