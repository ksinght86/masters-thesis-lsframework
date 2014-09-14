package tum.dss.thesis;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;

/**
 * Test area only! No functionality for the framework.
 * @author Florian Stallmann
 *
 */
public class Playground {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
//		double [][] matrix = new double[][]{{49.0, 1},{1, 49.0}};
//		
//		RealMatrix A = MatrixUtils.createRealMatrix(matrix);
//		EigenDecomposition test = new EigenDecomposition(A);
//		System.out.println( "test " + Arrays.toString(test.getRealEigenvalues()) );

//		BigReal[] columnData = {new BigReal(5.0), new BigReal(6.0)};
//		FieldMatrix<BigReal> bdtest = MatrixUtils.createColumnFieldMatrix(columnData);
//
//		BigReal[] columnData2 = {new BigReal(5.0), new BigReal(6.0)};
//		FieldMatrix<BigReal> bdtest2 = MatrixUtils.createColumnFieldMatrix(columnData2);
		
//		System.out.println(bdtest.transpose().multiply(bdtest2).get);
		
		DenseVector<Real> d1 = DenseVector.valueOf(Real.valueOf(1), Real.valueOf(2));
		DenseVector<Real> d2 = DenseVector.valueOf(Real.valueOf(3), Real.valueOf(4));
		DenseMatrix<Real> m1 = DenseMatrix.valueOf(d1, d2);
		DenseMatrix<Real> m2 = DenseMatrix.valueOf(d1);
		System.out.println(m1.times(Real.valueOf(-1.4)));
		System.out.println(m1.times(d1));
		System.out.println(m1.times(m2.transpose()));
		
		Real[] r = new Real[2];
		r[0] = Real.valueOf(0);
		r[1] = Real.valueOf(1);
		Real[] r2 = new Real[2];
		r2[0] = Real.valueOf(2);
		r2[1] = Real.valueOf(3);
		DenseMatrix<Real> center = DenseMatrix.valueOf(DenseVector.valueOf(r),DenseVector.valueOf(r2)).transpose();
		System.out.println(center);
		
	}

}
