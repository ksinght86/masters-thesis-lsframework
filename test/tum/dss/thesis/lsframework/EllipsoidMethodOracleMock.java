package tum.dss.thesis.lsframework;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.Vector;

import tum.dss.thesis.SeparationOracleInterface;

public class EllipsoidMethodOracleMock implements SeparationOracleInterface {
	public EllipsoidMethodDataMock data;
	public Vector<FloatingPoint> constraint_cut;
	public FloatingPoint gamma;
	
	public EllipsoidMethodOracleMock(EllipsoidMethodDataMock data) {
		this.data = data;
	}
	
	@Override
	public boolean ask(Vector<FloatingPoint> w) {
		//RealMatrix a = MatrixUtils.createColumnRealMatrix(w);
		for (int i = 0; i < data.C.getNumberOfRows(); i++) {
			if(data.C.getRow(i).times(w).isGreaterThan(data.d.get(i))) {
				constraint_cut = data.C.getRow(i);
				gamma = data.d.get(i);
				return false;
			}
		}
		gamma = w.times(data.o);
		return true;
	}

	@Override
	public int[] retrieveIntegerSolution() {
		return null;
	}

	@Override
	public Vector<FloatingPoint> getConstraintCut() {
		return constraint_cut;
	}

	@Override
	public Vector<FloatingPoint> getObjectiveCut() {
		return data.o;
	}

	@Override
	public double getIntegralityGap() {
		return 0;
	}

	@Override
	public FloatingPoint getGammaValue() {
		return gamma;
	}

	@Override
	public void setData(EllipsoidMethodData data) {
		// nothing to do here
	}

	@Override
	public boolean lastWzWasAskedBefore() {
		return false;
	}

	@Override
	public boolean lastWzWasNewSolution() {
		return false;
	}

	@Override
	public void setObjectiveAsGamma(boolean deep_gamma) {
		// nothing to do here
	}

}
