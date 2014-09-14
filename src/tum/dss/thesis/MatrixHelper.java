package tum.dss.thesis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Matrix;
import org.jscience.mathematics.vector.SparseMatrix;
import org.jscience.mathematics.vector.SparseVector;
import org.jscience.mathematics.vector.Vector;

/**
 * Some helper function for JScience matrices and vectors.
 * @author Florian Stallmann
 *
 */
public class MatrixHelper {
	/**
	 * Creates matrix from 2-dimensional double array.
	 * @param d 2d double array.
	 * @return  matrix.
	 */
	public static Matrix<FloatingPoint> createMatrix(final double[][] d) {
		List<DenseVector<FloatingPoint>> rows = new ArrayList<DenseVector<FloatingPoint>>();
		for (int i = 0; i < d.length; i++) {
			rows.add((DenseVector<FloatingPoint>) createVector(d[i]));
		}
		return DenseMatrix.valueOf(rows);
	}
	
	/**
	 * Creates column matrix from double array.
	 * @param d double array.
	 * @return  matrix.
	 */
	@SuppressWarnings("unchecked")
	public static Matrix<FloatingPoint> createColumnMatrix(final double[] d) {
		return DenseMatrix.valueOf((DenseVector<FloatingPoint>) createVector(d)).transpose();
	}
	
	/**
	 * Creates empty column matrix (i.e. vector filled with 0) with given dimension.
	 * @param dimension dimension of column matrix.
	 * @return          zero column matrix.
	 */
	@SuppressWarnings("unchecked")
	public static Matrix<FloatingPoint> createColumnMatrix(final int dimension) {
		return DenseMatrix.valueOf(SparseMatrix.valueOf(SparseVector.valueOf(dimension, FloatingPoint.ZERO, 0, FloatingPoint.ZERO)).transpose());
	}
	
	/**
	 * Creates column matrix from vector.
	 * @param v vector.
	 * @return  column matrix.
	 */
	@SuppressWarnings("unchecked")
	public static Matrix<FloatingPoint> createColumnMatrix(final Vector<FloatingPoint> v) {
		return DenseMatrix.valueOf((DenseVector<FloatingPoint>) v).transpose();
	}
	
	/**
	 * Creates vector from integer array.
	 * @param d int array.
	 * @return  vector.
	 */
	public static Vector<FloatingPoint> createVector(final int[] d) {
		FloatingPoint[] r = new FloatingPoint[d.length];
		for (int i = 0; i < d.length; i++) {
			r[i] = FloatingPoint.valueOf(d[i]);
		}
		return DenseVector.valueOf(r);
	}
	
	/**
	 * Creates vector from double array.
	 * @param d double array.
	 * @return  vector.
	 */
	public static Vector<FloatingPoint> createVector(final double[] d) {
		FloatingPoint[] r = new FloatingPoint[d.length];
		for (int i = 0; i < d.length; i++) {
			r[i] = FloatingPoint.valueOf(d[i]);
		}
		return DenseVector.valueOf(r);
	}
	
	/**
	 * Creates empty vector (filled with 0) with given dimension.
	 * @param dimension dimension of vector.
	 * @return          zero vector.
	 */
	public static Vector<FloatingPoint> createVector(final int dimension) {
		return DenseVector.valueOf(SparseVector.valueOf(dimension, FloatingPoint.ZERO, 0, FloatingPoint.ZERO));
	}
	
	/**
	 * Converts vector back to double array.
	 * @param v vector.
	 * @return  double array.
	 */
	public static double[] vectorToDouble(final Vector<FloatingPoint> v) {
		double[] d = new double[v.getDimension()];
		for (int i = 0; i < v.getDimension(); i++) {
			d[i] = v.get(i).doubleValue();
		}
		return d;
	}
	
	/**
	 * Normalizes a vector.
	 * <p>
	 * This is useful to compare whether vectors are parallel (v<sub>1</sub> = r * v<sub>2</sub>).
	 * The method will divide the vector by the square root of the squared vector: v / sqrt(v<sup>2</sup>).
	 * @param v vector.
	 * @return  normalized vector.
	 */
	public static Vector<FloatingPoint> normalizeVector(final Vector<FloatingPoint> v) {
		return v.times(v.times(v).sqrt().inverse()); // -> v / |v| = v / sqrt(v^2)
	}
	
	/**
	 * Gets hash code of a vector with rounding, so vectors with almost the same value due to
	 * numerical errors will get the same hash code.
	 * <p>
	 * This is used to detect if oracle is asked multiple times with the same wz-vector to call the approx.-algorithm.
	 * @param v vector.
	 * @return  integer as hash code.
	 */
	public static int getVectorHash(final Vector<FloatingPoint> v) {
		double[] d = new double[v.getDimension()];
		for (int i = 0; i < v.getDimension(); i++) {
//			if(v.get(i).isPositive()) {
				d[i] = Precision.round(v.get(i).doubleValue(), 10); //precision of 10 digits
//			} else {
//				d[i] = 0;
//			}
		}
		return Arrays.hashCode(d);
//		return Arrays.toString(d).hashCode();
	}
}
