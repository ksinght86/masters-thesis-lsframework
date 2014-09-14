package tum.dss.thesis.lsframework;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EllipsoidMethodTest5 {
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
		tester.setMaxIterations(10); //Terminate after 10 iterations and check is best feasible point is returned
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for central cut", new double[] {1.32042710833094, 2.36129578082208}, tester.getResult(), eps);
		assertEquals("Wrong feasible objective value for central cut", 6.043019, tester.getObjectiveValue(), eps);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for central cut", new double[] {1.32042710833094, 2.36129578082208}, tester.getResult(), eps);
		assertEquals("Wrong max objective value for central cut", 6.043019, tester.getObjectiveValue(), eps);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for central cut", new double[] {1.17711549332979, 1.28066758545176}, tester.getResult(), eps);
		assertEquals("Wrong min objective value for central cut", 3.738451, tester.getObjectiveValue(), eps);
	}

	@Test
	public void testStartAlgorithmShallowCut() {
		tester.setMaxIterations(10); //Terminate after 10 iterations and check is best feasible point is returned
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_SHALLOW);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for shallow cut", new double[] {1.04221907028273,  1.04221907028273}, tester.getResult(), eps);
		assertEquals("Wrong feasible objective value for shallow cut", 3.126657, tester.getObjectiveValue(), eps);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for shallow cut", new double[] {1.22891262997295,  2.34907398811427}, tester.getResult(), eps);
		assertEquals("Wrong max objective value for shallow cut", 5.927061, tester.getObjectiveValue(), eps);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for shallow cut", new double[] {1.04221907028273,  1.04221907028273}, tester.getResult(), eps);
		assertEquals("Wrong min objective value for shallow cut", 3.126657, tester.getObjectiveValue(), eps);
	}

	@Test
	public void testStartAlgorithmDeepCut() {
		tester.setMaxIterations(10); //Terminate after 10 iterations and check is best feasible point is returned
		//Set shallow cut
		tester.setCut(EllipsoidMethod.CUT_DEEP);
		//Feasible
		tester.startAlgorithm(EllipsoidMethod.MODE_FEASIBILITY);
		assertArrayEquals("Wrong feasible ellipsoid result for deep cut", new double[] {0.673565193828502,  2.04123831714188}, tester.getResult(), eps);
		assertEquals("Wrong feasible objective value for deep cut", 4.756042, tester.getObjectiveValue(), eps);
		//Max
		tester.startAlgorithm(EllipsoidMethod.MODE_MAXIMIZE);
		assertArrayEquals("Wrong max ellipsoid result for deep cut", new double[] {1.30223588169951,  2.06586502719065}, tester.getResult(), eps);
		assertEquals("Wrong max objective value for deep cut", 5.433966, tester.getObjectiveValue(), eps);
		//Min
		tester.startAlgorithm(EllipsoidMethod.MODE_MINIMIZE);
		assertArrayEquals("Wrong min ellipsoid result for deep cut", new double[] {1.23814189150925,  0.806632001110887}, tester.getResult(), eps);
		assertEquals("Wrong min objective value for deep cut", 2.851406, tester.getObjectiveValue(), eps);
	}

}
