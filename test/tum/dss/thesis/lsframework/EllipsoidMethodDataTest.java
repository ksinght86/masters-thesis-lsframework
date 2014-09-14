package tum.dss.thesis.lsframework;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import tum.dss.thesis.MatrixHelper;
import tum.dss.thesis.knapsack.KnapsackData1;

public class EllipsoidMethodDataTest {
	public EllipsoidMethodData tester;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		//Gurobi result test data (3 players and 4 items)
		double[][] testdata = new double[][]{
				{0, 0, 0.5, 0, 0},
				{0, 1, 0, 0.5, 0},
				{0, 0, 0, 0, 0}
		};
		tester = new EllipsoidMethodData(new KnapsackData1()); //valuations only important for x_valuations import
		tester.importLPResults(new LinearProgramMock(testdata));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testImportLPResults() {
		assertArrayEquals("Wrong imported x*", new double[]{0.5, 1, 0.5}, tester.x_star, 0);
		assertArrayEquals("Wrong imported valuations", new double[]{100, 90, 120}, tester.x_valuations, 0);

		assertEquals("Wrong number of players", 2, tester.getNumPlayers());
		assertEquals("Wrong number of subsets", 4, tester.getNumSubsets());

		assertEquals("Player 1 valuation has not been reset 0", 0, tester.getValuation(0, 0), 0);
		assertEquals("Player 1 valuation has not been reset 1", 0, tester.getValuation(0, 1), 0);
		assertEquals("Player 1 valuation has not been reset 2", 0, tester.getValuation(0, 2), 0);
		assertEquals("Player 1 valuation has not been reset 3", 0, tester.getValuation(0, 3), 0);
		assertEquals("Player 1 valuation has not been reset 4", 0, tester.getValuation(0, 4), 0);
		
		assertEquals("Player 2 valuation has not been reset 0", 0, tester.getValuation(1, 0), 0);
		assertEquals("Player 2 valuation has not been reset 1", 0, tester.getValuation(1, 1), 0);
		assertEquals("Player 2 valuation has not been reset 2", 0, tester.getValuation(1, 2), 0);
		assertEquals("Player 2 valuation has not been reset 3", 0, tester.getValuation(1, 3), 0);
		assertEquals("Player 2 valuation has not been reset 4", 0, tester.getValuation(1, 4), 0);
		
		assertEquals("Wrong whichItemSubset in E(0)", 2, tester.whichItemSubset(0));
		assertEquals("Wrong whichItemSubset in E(1)", 1, tester.whichItemSubset(1));
		assertEquals("Wrong whichItemSubset in E(2)", 3, tester.whichItemSubset(2));
		
		assertEquals("Wrong whichPlayer in E(0)", 0, tester.whichPlayer(0));
		assertEquals("Wrong whichPlayer in E(1)", 1, tester.whichPlayer(1));
		assertEquals("Wrong whichPlayer in E(2)", 1, tester.whichPlayer(2));
		
		assertEquals("Wrong mapping in E(0)", 0, tester.whichIndex(0, 2));
		assertEquals("Wrong mapping in E(1)", 1, tester.whichIndex(1, 1));
		assertEquals("Wrong mapping in E(2)", 2, tester.whichIndex(1, 3));
	}

	@Test
	public void testGetInitialCenter() {
		assertEquals("Incorrect initial center (default)", MatrixHelper.createVector(new double[]{0,0,0,1}), tester.getInitialCenter());
		assertEquals("Incorrect initial center (0-vector)", MatrixHelper.createVector(new double[]{0,0,0,0}), tester.getInitialCenter(1));
		assertEquals("Incorrect initial center (1-vector)", MatrixHelper.createVector(new double[]{1,1,1,1}), tester.getInitialCenter(2));
	}

	@Test
	public void testGetObjectiveValue() {
		//x*=[.5 1 .5]
		//6*.5/3 + 9*1/3 + 4.5*.5/3 + 2 = 6.75
		assertEquals("Wrong objective value calculated for wz1", 6.75, tester.getObjectiveValue(MatrixHelper.createVector(new double[]{6,9,4.5,2}), 3).doubleValue(), 0);
		
		//-6*.5/3 + 9*1/3 - 1.5*.5/3 - 3 = -1.25
		assertEquals("Wrong objective value calculated for wz2", -1.25, tester.getObjectiveValue(MatrixHelper.createVector(new double[]{-6,9,-1.5,-3}), 3).doubleValue(), 0);
	}

	@Test
	public void testUpdatePlayerValuation() {
		tester.updatePlayerValuation(MatrixHelper.createVector(new double[]{6,4,8,4}));
		assertEquals("Player 1 valuation incorrect for wz1, j=0", 0, tester.getValuation(0,0), 0);
		assertEquals("Player 1 valuation incorrect for wz1, j=2", 6, tester.getValuation(0,2), 0);
		assertEquals("Player 1 valuation incorrect for wz1, j=4", 6, tester.getValuation(0,4), 0);
		assertEquals("Player 2 valuation incorrect for wz1, j=1", 4, tester.getValuation(1,1), 0);
		assertEquals("Player 2 valuation incorrect for wz1, j=2", 4, tester.getValuation(1,2), 0);
		assertEquals("Player 2 valuation incorrect for wz1, j=3", 8, tester.getValuation(1,3), 0);
		assertEquals("Player 2 valuation incorrect for wz1, j=4", 8, tester.getValuation(1,4), 0);
//		assertArrayEquals("Player 1 valuation incorrect for wz1", new double[]{0,0,6,6,6}, tester.getPlayer(0).getValuations(), 0);
//		assertArrayEquals("Player 2 valuation incorrect for wz1", new double[]{0,4,4,8,8}, tester.getPlayer(1).getValuations(), 0);
		
		tester.updatePlayerValuation(MatrixHelper.createVector(new double[]{5,-3,6,1}));
		assertEquals("Player 1 valuation incorrect for wz2, j=2", 5, tester.getValuation(0,2), 0);
		assertEquals("Player 1 valuation incorrect for wz2, j=4", 5, tester.getValuation(0,4), 0);
		assertEquals("Player 2 valuation incorrect for wz2, j=1", 0, tester.getValuation(0,1), 0);
		assertEquals("Player 2 valuation incorrect for wz2, j=2", 0, tester.getValuation(1,2), 0);
		assertEquals("Player 2 valuation incorrect for wz2, j=3", 6, tester.getValuation(1,3), 0);
		assertEquals("Player 2 valuation incorrect for wz2, j=4", 6, tester.getValuation(1,4), 0);
//		assertArrayEquals("Player 1 valuation incorrect for wz2", new double[]{0,0,5,5,5}, tester.getPlayer(0).getValuations(), 0);
//		assertArrayEquals("Player 2 valuation incorrect for wz2", new double[]{0,0,0,6,6}, tester.getPlayer(1).getValuations(), 0);
		
		//exception.expect(IllegalArgumentException.class);
		tester.updatePlayerValuation(MatrixHelper.createVector(new double[]{6,4,2,4})); //valuation not monotone
		assertEquals("Player 1 valuation incorrect for wz3, j=2", 6, tester.getValuation(0,2), 0);
		assertEquals("Player 1 valuation incorrect for wz3, j=3", 6, tester.getValuation(0,3), 0);
		assertEquals("Player 1 valuation incorrect for wz3, j=4", 6, tester.getValuation(0,4), 0);
		assertEquals("Player 2 valuation incorrect for wz3, j=1", 4, tester.getValuation(1,1), 0);
		assertEquals("Player 2 valuation incorrect for wz3, j=3", 4, tester.getValuation(1,3), 0);
//		assertArrayEquals("Player 1 valuation incorrect for wz3", new double[]{0,0,6,6,6}, tester.getPlayer(0).getValuations(), 0);
//		assertArrayEquals("Player 2 valuation incorrect for wz3", new double[]{0,4,4,4,4}, tester.getPlayer(1).getValuations(), 0);
	}

	@Test
	public void testAddIntegerSolution() {
		assertTrue("Integer solution not defined as new", tester.addIntegerSolution(new int[]{1,1,1}, 1) );
		assertEquals("Integer solution not added", 1, tester.getNumIntegerSolutions());
		
		assertFalse("Integer solution wrongly defined as new", tester.addIntegerSolution(new int[]{1,1,1}, 2) );
		assertEquals("Duplicate check not working", 1, tester.getNumIntegerSolutions());
		
		exception.expect(IllegalArgumentException.class); //too many int values
		tester.addIntegerSolution(new int[]{1,1,1,1}, 3);
	}

	@Test
	public void testAddManualIntegerSolutions() {
		tester.addManualIntegerSolutions(true); //x*=[.5 1 .5]
		assertEquals("Wrong number of solutions", 4, tester.getNumIntegerSolutions());
		assertArrayEquals("No zero vector", new int[3], tester.getIntegerSolution(0).getSolution());
		assertArrayEquals("Wrong manual solution 1", new int[]{1,0,0}, tester.getIntegerSolution(1).getSolution());
		assertArrayEquals("Wrong manual solution 2", new int[]{0,0,1}, tester.getIntegerSolution(2).getSolution());
		assertArrayEquals("Wrong manual solution 3", new int[]{0,1,0}, tester.getIntegerSolution(3).getSolution());
	}
	
	@Test
	public void testGetDimension() {
		assertEquals("Wrong dimension", 4, tester.getDimension());
	}

}
