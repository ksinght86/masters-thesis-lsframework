package tum.dss.thesis.lsframework;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
	public PlayerMock tester;

	@Before
	public void setUp() throws Exception {
		tester = new PlayerMock("name", 5);
		tester.setItemsetHandler(new ItemsetHandlerMock(5));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetValuation() {
		tester.setValuation(1,1); //[1=1,2=1,3=1,4=1,5=1]
		assertEquals("Valuation update 1,1 failed", 1, tester.getValuation(1), 0);
		assertEquals("Valuation 2 monotone 1,1 failed", 1, tester.getValuation(2), 0);
		assertEquals("Valuation 3 monotone 1,1 failed", 1, tester.getValuation(3), 0);
		
		tester.setValuation(3,5); //[1=1,2=1,3=5,4=5,5=5]
		assertEquals("Valuation update 3,5 failed", 5, tester.getValuation(3), 0);
		assertEquals("Valuation 4 monotone 3,5 failed", 5, tester.getValuation(4), 0);
		assertEquals("Valuation 1 should not change", 1, tester.getValuation(1), 0);
		
		tester.setValuation(4,2); //[1=1,2=1,3=5,4=5,5=5] (nothing changed)
		assertEquals("Valuation 3 should not change", 5, tester.getValuation(3), 0);
		assertEquals("Valuation 4 updated to non-monotone version", 5, tester.getValuation(4), 0);
		assertEquals("Valuation 5 updated to non-monotone version", 5, tester.getValuation(5), 0);
		
		tester.setValuation(3,2); //[1=1,2=1,3=2,4=2,5=2]
		assertEquals("Valuation 2 should not change", 1, tester.getValuation(2), 0);
		assertEquals("Valuation 3 did not decrease", 2, tester.getValuation(3), 0);
		assertEquals("Valuation 4 did not decrease", 2, tester.getValuation(4), 0);
		assertEquals("Valuation 5 did not decrease", 2, tester.getValuation(5), 0);
		
		tester.setValuation(1,10); //[1=10,2=10,3=10,4=10,5=10]
		assertEquals("Valuation update 1,10 failed", 10, tester.getValuation(1), 0);
		assertEquals("Valuation 2 monotone 1,10 failed", 10, tester.getValuation(2), 0);
		assertEquals("Valuation 3 monotone 1,10 failed", 10, tester.getValuation(3), 0);
		assertEquals("Valuation 4 monotone 1,10 failed", 10, tester.getValuation(4), 0);
		assertEquals("Valuation 5 monotone 1,10 failed", 10, tester.getValuation(5), 0);
		
		tester.setValuation(3,4); //[1=10,2=10,3=10,4=10,5=10] (nothing changed)
		assertEquals("Valuation 1 decreased", 10, tester.getValuation(1), 0);
		assertEquals("Valuation 2 decreased", 10, tester.getValuation(2), 0);
		assertEquals("Valuation 3 decreased", 10, tester.getValuation(3), 0);
		assertEquals("Valuation 4 decreased", 10, tester.getValuation(4), 0);
		assertEquals("Valuation 5 decreased", 10, tester.getValuation(5), 0);
	}
	
	@Test
	public void testResetValuation() {
		tester.setValuation(1,1);
		assertEquals("Valuation update 1,1 failed", 1, tester.getValuation(1), 0);
		
		tester.resetValuations();
		assertEquals("Valuation reset failed (1)", 0, tester.getValuation(1), 0);
		assertEquals("Valuation reset failed (2)", 0, tester.getValuation(3), 0);
		assertEquals("Valuation reset failed (3)", 0, tester.getValuation(5), 0);
	}
	
	@Test
	public void testFindMostValuableSubset() {
		tester.setValuation(1,1);
		assertEquals("Wrong subset for j=3", 1, tester.findMostValuableSubset(3), 0);
		
		tester.setValuation(3,4);
		assertEquals("Wrong subset for j=5", 3, tester.findMostValuableSubset(5), 0);
		
		tester.setValuation(1,8); //due to monotonization j=3 also gets updated
		assertEquals("Wrong subset for j=3 (2)", 1, tester.findMostValuableSubset(3), 0);
		assertEquals("Wrong subset for j=5 (2)", 3, tester.findMostValuableSubset(5), 0);
	}
	
	@Test
	public void testDropValuations() {
		tester.setValuations(new double[]{0, 2, 2, 4});
		assertEquals("Setting valuations (1) failed", 2, tester.getValuation(2), 0);
		assertEquals("Setting valuations (2) failed", 4, tester.getValuation(3), 0);

		tester.dropOriginalValuations();
		assertEquals("Dropping valuations (1) failed", 0, tester.getValuation(2), 0);
		assertEquals("Dropping valuations (2) failed", 0, tester.getValuation(3), 0);
	}

}
