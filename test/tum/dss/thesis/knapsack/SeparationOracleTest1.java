package tum.dss.thesis.knapsack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tum.dss.thesis.LinearProgramInterface;
import tum.dss.thesis.MatrixHelper;
import tum.dss.thesis.MechanismDataInterface;
import tum.dss.thesis.lsframework.EllipsoidMethodData;
import tum.dss.thesis.lsframework.LinearProgramMock;

public class SeparationOracleTest1 {
	public SeparationOracle tester;

	@Before
	public void setUp() throws Exception {
		MechanismDataInterface knp1 = new KnapsackData1();
		EllipsoidMethodData elld = new EllipsoidMethodData(knp1);
		LinearProgramInterface lp = new LinearProgramMock(new double[][]{{0,0,0,1,0},{0,1,0,0,0},{0,0,0,0,0}});
		elld.importLPResults(lp);
		tester = new SeparationOracle(elld);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRetrieveIntegerSolution() {
		assertArrayEquals("Incorrect integer solution", new int[]{0,0}, tester.retrieveIntegerSolution());
	}

	@Test
	public void testAsk() {
		assertTrue( tester.ask(MatrixHelper.createVector(new double[]{0,0,1})) );
		assertFalse( tester.ask(MatrixHelper.createVector(new double[]{0.2,0.2,1.4})) );
	}

	@Test
	public void testGetConstraintCut() {
		tester.ask(MatrixHelper.createVector(new double[]{0.2,0.2,1.4}));
		assertArrayEquals("Incorrect constraint cut", new double[]{1,1,1}, MatrixHelper.vectorToDouble(tester.getConstraintCut()), 0.0);
	}

	@Test
	public void testGetObjectiveCut() {
		tester.ask(MatrixHelper.createVector(new double[]{0,0,1}));
		assertArrayEquals("Incorrect objective cut", new double[]{0.5,0.5,1}, MatrixHelper.vectorToDouble(tester.getObjectiveCut()), 0.0);
	}

	@Test
	public void testGetIntegralityGap() {
		assertEquals("Integrality gap invalid", 2.0, tester.getIntegralityGap(), 0.0);
	}

}
