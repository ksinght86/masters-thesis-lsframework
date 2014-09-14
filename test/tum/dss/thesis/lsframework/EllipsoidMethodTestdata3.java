package tum.dss.thesis.lsframework;

import tum.dss.thesis.MatrixHelper;

public class EllipsoidMethodTestdata3 extends EllipsoidMethodDataMock {
	private double[][]	constraints = {{-1, -3, -4},{-8, -2, -3},{-1, 0, 0},{0, -1, 0},{0, 0, -1}};
	private double[] values = {-4,-7,0,0,0};
	private double[] objective = {-1,-2,-3};
	
	public EllipsoidMethodTestdata3() {
		C = MatrixHelper.createMatrix(constraints);
		d = MatrixHelper.createVector(values);
		o = MatrixHelper.createVector(objective);
	}
}
