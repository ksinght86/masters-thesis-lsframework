package tum.dss.thesis.lsframework;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tum.dss.thesis.MatrixHelper;

public class SeparationOracleTest {
	public SeparationOracleTestdata testdata;
	public SeparationOracleMock tester;

	@Before
	public void setUp() throws Exception {
		testdata = new SeparationOracleTestdata();
		tester = new SeparationOracleMock();
		tester.setData(testdata);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAskAndCuts() {
		double[] wz = new double[tester.ell_data.getDimension()];
		
		Arrays.fill(wz, 0);
		assertTrue("Oracle gave wrong answer for point in polytope", tester.ask(MatrixHelper.createVector(wz)));
		assertFalse("No solution should have been found", tester.lastWzWasNewSolution());
		assertEquals("Wrong gamma value for objective cut", 1.0, tester.getGammaValue().doubleValue(), 0);
		assertArrayEquals("Wrong objective cut", new double[]{0.33,0.33,0.33,0.33,0.33,0.33,1}, MatrixHelper.vectorToDouble(tester.getObjectiveCut()), 0.01);
		
		Arrays.fill(wz, 1);
		assertFalse("Oracle gave wrong answer for point not in polytope", tester.ask(MatrixHelper.createVector(wz)));
		assertTrue("Solution should have been new", tester.lastWzWasNewSolution());
		assertEquals("Wrong gamma value for constraint cut", 1.0, tester.getGammaValue().doubleValue(), 0);
		assertArrayEquals("Wrong constraint cut", new double[]{1,0,1,0,0,1,1}, MatrixHelper.vectorToDouble(tester.getConstraintCut()), 0);
		assertArrayEquals("Wrong updated player valuation for constraint cut", wz, testdata.updatedPlayerValuationTest, 0);
	}
	
	@Test
	public void testTranslateIntegerSolution() {
		double[] wz = new double[tester.ell_data.getDimension()];
		int[] sol = new int[tester.ell_data.getNumPlayers()];
	
		//Mapping in testdata:
		//0->[1,2]   1->[1,3,5]   2->[1]
		
		Arrays.fill(sol, 1);
		Arrays.fill(wz, 0);
		assertArrayEquals("Wrong translation of integer solution (1)", new int[]{0,0,0,0,0,0}, tester.translateIntegerSolution(sol, MatrixHelper.createVector(wz)));
		
		Arrays.fill(wz, 1);
		assertArrayEquals("Wrong translation of integer solution (2)", new int[]{1,0,1,0,0,1}, tester.translateIntegerSolution(sol, MatrixHelper.createVector(wz)));

		sol[0] = 2;
		sol[1] = 5;
		assertArrayEquals("Wrong translation of integer solution (3)", new int[]{0,1,0,0,1,1}, tester.translateIntegerSolution(sol, MatrixHelper.createVector(wz)));
		
		wz[1] = -1; //0->2
		assertArrayEquals("Wrong translation of integer solution (4)", new int[]{0,0,0,0,1,1}, tester.translateIntegerSolution(sol, MatrixHelper.createVector(wz)));

		sol[1] = 4; //will lead to 1->3
		assertArrayEquals("Wrong translation of integer solution (5)", new int[]{0,0,0,1,0,1}, tester.translateIntegerSolution(sol, MatrixHelper.createVector(wz)));

		wz[3] = -1; //1->3 (will lead to 1->1 because wz[2]=1 > wz[3]=-1)
		assertArrayEquals("Wrong translation of integer solution (6)", new int[]{0,0,1,0,0,1}, tester.translateIntegerSolution(sol, MatrixHelper.createVector(wz)));
	}
	
	@Test
	public void testSameAskedVector() {
		double[] wz = new double[tester.ell_data.getDimension()];
		
		//Initially wz_asked should be empty
		assertEquals("Wrong wz_asked size 1", 0, tester.wz_asked.size());
		
		Arrays.fill(wz, 1);
		tester.ask(MatrixHelper.createVector(wz));

		assertFalse("Permutation check failed", tester.lastWzWasAskedBefore());
		assertEquals("Wrong wz_asked size 1", 1, tester.wz_asked.size());
		
		//Ask the same vector another time to give a warning
		tester.ask(MatrixHelper.createVector(wz));
		assertTrue("Permutation check failed", tester.lastWzWasAskedBefore());
		assertEquals("Wrong wz_asked size 1", 1, tester.wz_asked.size());
		
		Arrays.fill(wz, 2);
		//Ask vector*2 should also give warning
		tester.ask(MatrixHelper.createVector(wz));
		assertTrue("Permutation check failed", tester.lastWzWasAskedBefore());
		assertEquals("Wrong wz_asked size 1", 1, tester.wz_asked.size());
	}

}
