package tum.dss.thesis.lsframework;

import tum.dss.thesis.LinearProgramInterface;
import tum.dss.thesis.MechanismDataInterface;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class LinearProgramMock implements LinearProgramInterface {
	public double[][] x_double;
	public ListMultimap<Integer, Integer> sol_used = ArrayListMultimap.create();

	public LinearProgramMock(double[][] x) {
		x_double = x;
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[i].length; j++) {
				if(x[i][j] > 0) {
					sol_used.put(i, j);
				}
			}
		}
	}
	
	@Override
	public void createLP(boolean relax) {
		// nothing to do in mock
	}

	@Override
	public void createLP() {
		// nothing to do in mock
	}

	@Override
	public void solveLP() {
		// nothing to do in mock
	}

	@Override
	public double getResult(int i, int j) {
		return x_double[i][j];
	}

	@Override
	public ListMultimap<Integer, Integer> getSolUsed() {
		return sol_used;
	}

	@Override
	public void setData(MechanismDataInterface data) {
		// nothing to do in mock
	}

	@Override
	public double getObjective() {
		return 0;
	}

}
