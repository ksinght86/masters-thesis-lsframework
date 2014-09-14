package tum.dss.thesis.lsframework;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.Matrix;
import org.jscience.mathematics.vector.Vector;

import tum.dss.thesis.ItemsetHandlerInterface;
import tum.dss.thesis.MechanismDataInterface;
import tum.dss.thesis.PlayerInterface;

public abstract class EllipsoidMethodDataMock implements MechanismDataInterface {
	public Matrix<FloatingPoint> C;
	public Vector<FloatingPoint> d;
	public Vector<FloatingPoint> o;

	@Override
	public int getNumSubsets() {
		return 0;
	}

	@Override
	public int getNumPlayers() {
		return 0;
	}

	@Override
	public double getValuation(int i, int j) {
		return 0;
	}

	@Override
	public void addPlayer(PlayerInterface player) {
		// nothing to do here
		
	}

	@Override
	public PlayerInterface getPlayer(int i) {
		return null;
	}

	@Override
	public int getNumItemsInSubset(int j) {
		return 0;
	}

	@Override
	public ItemsetHandlerInterface getItemsetHandler() {
		return null;
	}

	@Override
	public void setItemsetHandler(ItemsetHandlerInterface itemset) {
		// nothing to do here
		
	}
}
