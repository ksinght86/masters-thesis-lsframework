package tum.dss.thesis.lsframework;

import static org.junit.Assert.assertArrayEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EllipsoidMethodTest4 {
	public EllipsoidMethod tester;

	@Before
	public void setUp() throws Exception {
		EllipsoidMethodDataMock data = new EllipsoidMethodTestdata4();
		tester = new EllipsoidMethod(new EllipsoidMethodOracleMock(data), data.C.getNumberOfColumns());
		tester.setRadius(100000);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartAlgorithmCentralCut() {
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for central cut", new double[] {0.49, 20243.49}, tester.getResult(), 0.01);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for central cut", new double[] {0.4999, 99999}, tester.getResult(), 0.01);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for central cut", new double[] {0.49, 0.1}, tester.getResult(), 0.01);
	}

	@Test
	public void testStartAlgorithmShallowCut() {
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_SHALLOW);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for shallow cut", new double[] {0.49, 44539.85}, tester.getResult(), 0.01);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for shallow cut", new double[] {0.4999, 99999}, tester.getResult(), 0.01);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for shallow cut", new double[] {0.49, 0.1}, tester.getResult(), 0.01);
	}

	@Test
	public void testStartAlgorithmDeepCut() {
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_DEEP);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for deep cut", new double[] {0.49, 65718.62}, tester.getResult(), 0.01);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for deep cut", new double[] {0.4999, 99999}, tester.getResult(), 0.01);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for deep cut", new double[] {0.49, 0.1}, tester.getResult(), 0.01);
	}

}
