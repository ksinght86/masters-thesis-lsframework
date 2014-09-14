package tum.dss.thesis.lsframework;

import java.util.Arrays;

public class SeparationOracleMock extends AbstractSeparationOracle {
	@Override
	public int[] retrieveIntegerSolution() {
		int[] sol = new int[ell_data.getNumPlayers()];
		Arrays.fill(sol, 1);
		return sol;
	}

	@Override
	public double getIntegralityGap() {
		return 3;
	}

}
