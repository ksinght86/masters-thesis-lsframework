package tum.dss.thesis.lsframework;

import java.util.Arrays;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.Vector;

import tum.dss.thesis.MatrixHelper;


@SuppressWarnings("serial")
public class SeparationOracleTestdata extends EllipsoidMethodData {
	public double[] updatedPlayerValuationTest;
	
	public SeparationOracleTestdata() {
		super();
		
		//data needed to calculate cuts
		x_star = new double[6]; //6 values in x*
		Arrays.fill(x_star, 1.0);
		
		//data needed to translate solution (5 items, 3 players)
		itemset_handler = new ItemsetHandlerMock(5);
		addMapping(0, 0, 1);
		addMapping(1, 0, 2);
		addMapping(2, 1, 1);
		addMapping(3, 1, 3);
		addMapping(4, 1, 5);
		addMapping(5, 2, 1);
	}
	
	@Override
	public void updatePlayerValuation(Vector<FloatingPoint> wz) {
		updatedPlayerValuationTest = MatrixHelper.vectorToDouble(wz);
	}
	
	@Override
	public int getNumPlayers() {
		return 3;
	}
}
