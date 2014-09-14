package tum.dss.thesis.lsframework;

import tum.dss.thesis.MatrixHelper;

public class EllipsoidMethodTestdata4 extends EllipsoidMethodDataMock {
	private double[][]	constraints = {{1,0},{0,1},{-1, 0},{0, -1}};
	private double[] values = {0.5,99999,-0.49,-0.1};
	private double[] objective = {1,2};
	
	public EllipsoidMethodTestdata4() {
		C = MatrixHelper.createMatrix(constraints);
		d = MatrixHelper.createVector(values);
		o = MatrixHelper.createVector(objective);
	}
}
