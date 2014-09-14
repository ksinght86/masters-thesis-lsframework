package tum.dss.thesis.knapsack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataGeneratorTest {
	public KnapsackDataGenerator tester;
	int items = 100;
	int players = 3;

	@Before
	public void setUp() throws Exception {
		tester = new KnapsackDataGenerator(items);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateSingleMindedData() {
		tester.generateSingleMindedData(players);
		for (int i = 0; i < players; i++) {
			double val = 0;
			for (int j = 0; j <= items; j++) {
				if(val == 0) { //Once val is not 0 anymore every valuation needs to be the same
					val = tester.getValuation(i, j);
				}
				assertEquals("Not single minded for i=" + i + " and j=" + j, val, tester.getValuation(i, j), 0);
			}
		}
	}

	@Test
	public void testGenerateKMindedData() {
		int k=3;
		tester.generateKMindedData(players, k);
		for (int i = 0; i < players; i++) {
			double prev = 0;
			double current;
			int count_k = 0;
			for (int j = 0; j <= items; j++) {
				current = tester.getValuation(i, j);
				if(prev != current) {
					count_k++;
					assertTrue("Not increasing for i=" + i + " and j=" + j, current > prev);
				}
				prev = current;
			}
			assertEquals("Not k-minded for i=" + i, k, count_k);
		}
	}

	@Test
	public void testGenerateNonSingleMindedDataDec() {
		tester.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_DECREASING);
		tester.generateNonSingleMindedData(players);
		for (int i = 0; i < players; i++) {
			double prev = (tester.getValuation(i, 1) - tester.getValuation(i, 0));
			double current;
			for (int j = 2; j <= items; j++) {
				current = (tester.getValuation(i, j) - tester.getValuation(i, j-1));
//				System.out.println(prev + " " + current);
				assertTrue("Not decreasing for i=" + i + " and j=" + j, current < prev);
				prev = current;
			}
		}
	}

	@Test
	public void testGenerateNonSingleMindedDataInc() {
		tester.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_INCREASING);
		tester.generateNonSingleMindedData(players);
		for (int i = 0; i < players; i++) {
			double prev = (tester.getValuation(i, 1) - tester.getValuation(i, 0));
			double current;
			for (int j = 2; j <= items; j++) {
				current = (tester.getValuation(i, j) - tester.getValuation(i, j-1));
//				System.out.println(prev + " " + current);
				assertTrue("Not increasing for i=" + i + " and j=" + j, current > prev);
				prev = current;
			}
		}
	}

	@Test
	public void testGenerateNonSingleMindedDataCon() {
		tester.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_CONSTANT);
		tester.generateNonSingleMindedData(players);
		for (int i = 0; i < players; i++) {
			double prev = (tester.getValuation(i, 1) - tester.getValuation(i, 0));
			double current;
			for (int j = 2; j <= items; j++) {
				current = (tester.getValuation(i, j) - tester.getValuation(i, j-1));
				assertEquals("Not constant for i=" + i + " and j=" + j, prev, current, 0.0001);
				prev = current;
			}
		}
	}

}
