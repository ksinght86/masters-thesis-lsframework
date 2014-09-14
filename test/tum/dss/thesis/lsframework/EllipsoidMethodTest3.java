package tum.dss.thesis.lsframework;

import static org.junit.Assert.assertArrayEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EllipsoidMethodTest3 {
	public EllipsoidMethod tester;

	@Before
	public void setUp() throws Exception {
		EllipsoidMethodDataMock data = new EllipsoidMethodTestdata3();
		tester = new EllipsoidMethod(new EllipsoidMethodOracleMock(data), data.C.getNumberOfColumns());
		tester.setRadius(50);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartAlgorithmCentralCut() {
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for central cut", new double[] {2.45, 7.35, 9.81}, tester.getResult(), 0.01);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for central cut", new double[] {0.59, 1.14, 0}, tester.getResult(), 0.01);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for central cut", new double[] {15.52, 25.97, 41.5}, tester.getResult(), 0.01);
	}

	@Test
	public void testStartAlgorithmShallowCut() {
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_SHALLOW);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for shallow cut", new double[] {0.61, 1.84, 2.45}, tester.getResult(), 0.01);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for shallow cut", new double[] {0.59, 1.14, 0}, tester.getResult(), 0.01);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for shallow cut", new double[] {14.18, 27.1, 41.27}, tester.getResult(), 0.01);
	}

	@Test
	public void testStartAlgorithmDeepCut() {
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_DEEP);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for deep cut", new double[] {2.57, 7.7, 10.27}, tester.getResult(), 0.01);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for deep cut", new double[] {0.59, 1.14, 0}, tester.getResult(), 0.01);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for deep cut", new double[] {15.6, 25.9, 41.5}, tester.getResult(), 0.01);
	}

}
