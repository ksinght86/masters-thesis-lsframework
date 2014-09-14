package tum.dss.thesis.lsframework;

import tum.dss.thesis.MatrixHelper;

public class EllipsoidMethodTestdata1 extends EllipsoidMethodDataMock {
	private double[][]	constraints = {{-1,-1},{3,0},{-2,2}};
	private double[] values = {-2,4,3};
	private double[] objective = {1,2};
	
	public EllipsoidMethodTestdata1() {
		C = MatrixHelper.createMatrix(constraints);
		d = MatrixHelper.createVector(values);
		o = MatrixHelper.createVector(objective);
	}
}
