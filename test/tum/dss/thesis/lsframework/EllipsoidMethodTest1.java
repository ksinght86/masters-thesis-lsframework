package tum.dss.thesis.lsframework;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EllipsoidMethodTest1 {
	public EllipsoidMethod tester;
	private final double eps = 1e-5;

	@Before
	public void setUp() throws Exception {
		EllipsoidMethodDataMock data = new EllipsoidMethodTestdata1();
		tester = new EllipsoidMethod(new EllipsoidMethodOracleMock(data), data.C.getNumberOfColumns());
		tester.setRadius(7);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartAlgorithmCentralCut() {
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for central cut", new double[] {1.32042710833094, 2.36129578082208}, tester.getResult(), eps);
		assertEquals("Wrong feasible objective value for central cut", 6.043019, tester.getObjectiveValue(), eps);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for central cut", new double[] {1.33333301151465, 2.83333293199349}, tester.getResult(), eps);
		assertEquals("Wrong max objective value for central cut", 6.999999, tester.getObjectiveValue(), eps);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for central cut", new double[] {1.33333076715299, 0.666669459065606}, tester.getResult(), eps);
		assertEquals("Wrong min objective value for central cut", 2.666670, tester.getObjectiveValue(), eps);
	}

	@Test
	public void testStartAlgorithmShallowCut() {
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_SHALLOW);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for shallow cut", new double[] {1.04221907028273,  1.04221907028273}, tester.getResult(), eps);
		assertEquals("Wrong feasible objective value for shallow cut", 3.126657, tester.getObjectiveValue(), eps);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for shallow cut", new double[] {1.33333312614357,  2.83333309555674}, tester.getResult(), eps);
		assertEquals("Wrong max objective value for shallow cut", 6.999999, tester.getObjectiveValue(), eps);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for shallow cut", new double[] {1.33333253254753,  0.66666747886465}, tester.getResult(), eps);
		assertEquals("Wrong min objective value for shallow cut", 2.666670, tester.getObjectiveValue(), eps);
	}

	@Test
	public void testStartAlgorithmDeepCut() {
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_DEEP);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for deep cut", new double[] {0.673565193828502,  2.04123831714188}, tester.getResult(), eps);
		assertEquals("Wrong feasible objective value for deep cut", 4.756042, tester.getObjectiveValue(), eps);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for deep cut", new double[] {1.3333320167592,  2.83333150798832}, tester.getResult(), eps);
		assertEquals("Wrong max objective value for deep cut", 6.999999, tester.getObjectiveValue(), eps);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for deep cut", new double[] {1.3333318594569,  0.666669702034136}, tester.getResult(), eps);
		assertEquals("Wrong min objective value for deep cut", 2.666670, tester.getObjectiveValue(), eps);
	}

}
