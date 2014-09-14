package tum.dss.thesis.lsframework;

import tum.dss.thesis.MatrixHelper;

public class EllipsoidMethodTestdata2 extends EllipsoidMethodDataMock {
	private double[][]	constraints = {{1, 1, 0},{0, 1, 1},{-1, 0, 0},{0, -1, 0},{0, 0, -1}};
	private double[] values = {1,1,0,0,0};
	private double[] objective = {1,2,3};
	
	public EllipsoidMethodTestdata2() {
		C = MatrixHelper.createMatrix(constraints);
		d = MatrixHelper.createVector(values);
		o = MatrixHelper.createVector(objective);
	}
}
