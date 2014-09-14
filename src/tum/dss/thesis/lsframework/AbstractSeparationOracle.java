package tum.dss.thesis.lsframework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.Vector;

import tum.dss.thesis.MatrixHelper;
import tum.dss.thesis.SeparationOracleInterface;

/**
 * Abstract separation oracle which implements all common functions among different algorithms.
 * @author Florian Stallmann
 *
 */
public abstract class AbstractSeparationOracle implements
		SeparationOracleInterface {
	private static final Logger log = Logger.getLogger( AbstractSeparationOracle.class.getName() );
	
	/** Integrality gap given by child classes */
	protected static double ALPHA;
	
	/** Data helper class for ellipsoid method */
	protected EllipsoidMethodData ell_data;
	
	/** Objective cut (sliding objective method) */
	protected Vector<FloatingPoint> objective_cut;
	
	/** Constraint cut (using alpha-algorithm) after asking oracle */
	protected Vector<FloatingPoint> constraint_cut;
	
	/** Gamma value needed for deep cut */
	protected FloatingPoint gamma = FloatingPoint.ONE;
	
	/** Save all wz vectors that have been asked and used with alpha-algorithm */
	protected List<Integer> wz_asked = new ArrayList<Integer>(100);
	
	/** Set to true if the last call of <code>ask()</code>, the vector wz has been asked before */
	protected boolean last_asked_before = false;
	/** Set to true if the last call of <code>ask()</code>, a new solution was found */
	protected boolean last_solution_added = false;
	
	/** Set to true if the objective value instead of 1 should be used as gamma for deep cut */
	protected boolean use_objective_as_gamma = false;
	
	/** Counts number of times oracle is being asked (= number of iterations) */
	private int iteration = 0;
	
	/**
	 * Initializes oracle without data.
	 */
	public AbstractSeparationOracle() {
		ALPHA = getIntegralityGap(); //Force subclass to set integrality gap
	}
	
	/**
	 * Initializes oracle with ellipsoid compatible data.
	 * @param data ellipsoid method data.
	 */
	public AbstractSeparationOracle(final EllipsoidMethodData data) {
		this();
		setData(data);
	}
	
	@Override
	public void setData(final EllipsoidMethodData data) {
		this.ell_data = data;
		
		//reset variables
		iteration = 0;
		wz_asked.clear();
		
		//calculate objective cut
		double[] objective_cut_tmp = new double[data.getDimension()];
		for (int i = 0; i < data.getDimension()-1; i++) {
			// Lemma 3.4 par. 2
			objective_cut_tmp[i] = data.x_star[i] / ALPHA;
		}
		
		//Parameter of z (last entry) is always 1
		objective_cut_tmp[data.getDimension()-1]  = 1;
		objective_cut = MatrixHelper.createVector(objective_cut_tmp);
	}
	
	@Override
	public boolean ask(final Vector<FloatingPoint> wz) {
		iteration++; //Iteration counter
		last_asked_before   = false;
		last_solution_added = false;
		
		//DEBUG: output wz
		log.fine("Oracle asked in iteration " + iteration + " with wz = " + wz);
		
		//Calculate objective value to check if wz is in polytope
		FloatingPoint obj = ell_data.getObjectiveValue(wz, ALPHA);
		
		// Lemma 3.4 par. 2
		if(obj.isGreaterThan(FloatingPoint.ONE)) {
			//Not in polytope: constraint cut
			
			//Check if this wz-vector has been used before as valuation
			int norm_wz = MatrixHelper.getVectorHash( MatrixHelper.normalizeVector(wz) );
			if( wz_asked.contains(norm_wz) ) {
				last_asked_before = true;
				log.warning("Current wz has been used before: Would permutation be reasonable?");
			} else {
				wz_asked.add(norm_wz);
			}
			
			//Find $x_l$ and corresponding violated constraint (Claim 3.3)
			ell_data.updatePlayerValuation(wz);
			//Retrieve approximated integer solution (e.g. by greedy algorithm) and translate to valid solution
			int[] sol = translateIntegerSolution(retrieveIntegerSolution(), wz);
			//Save solution for later
			if( ell_data.addIntegerSolution(sol, iteration) ) {
				last_solution_added = true; //Solution is a new one
			}
			//Calculate constraint cut (last entry doesn't change)
			int[] constraint_cut_tmp = new int[ell_data.getDimension()];
			System.arraycopy(sol, 0, constraint_cut_tmp, 0, ell_data.getDimension()-1);
			constraint_cut_tmp[ell_data.getDimension()-1] = 1;
			constraint_cut = MatrixHelper.createVector(constraint_cut_tmp);
			//gamma value for deep cut is always 1 for constraint cut as we are adding the constraint (5)
			//however, changing this value (e.g. to objective value) might lead to interesting results
			gamma = FloatingPoint.ONE;
//			gamma = obj;
			return false;
		}
		//In polytope: sliding objective cut
		//gamma value for deep cut: usually this is the objective value of the current center
		//however, the framework suggest to use a certain half space (lemma 3.4 proof last sentence) with value of 1
		gamma = (use_objective_as_gamma ? obj : FloatingPoint.ONE);
		return true;
	}

	@Override
	public Vector<FloatingPoint> getConstraintCut() {
		return constraint_cut;
	}

	@Override
	public Vector<FloatingPoint> getObjectiveCut() {
		return objective_cut;
	}

	@Override
	public FloatingPoint getGammaValue() {
		return gamma;
	}
	
	/**
	 * Translates the integer solution using claim 3.2 and 3.3 to a compatible solution.
	 * <p>
	 * This is necessary as the approximation algorithm might return a solution which is not
	 * in set E and therefore it cannot be used for the decomposition. Additionally we need to
	 * reverse the changes we did during monotonization. See claim 3.2 and especially the second
	 * part of the proof of claim 3.3 for details.
	 * @param sol integer solution of separation oracle.
	 * @param wz  current center.
	 * @return    translated integer solution x<sup>l</sup> added to found solutions.
	 */
	protected int[] translateIntegerSolution(final int[] sol, final Vector<FloatingPoint> wz) {
		//Translate solution using Claim 3.3 first and then 3.2
		//Parameter sol[i] == $\tilde{x}$
		int[] x_l = new int[ell_data.getDimension()-1];
		for (int i = 0; i < sol.length; i++) {
			//if player gets nothing $x_l$ value just stays 0
			if(sol[i] == 0) { continue; }
			
			int key = -1;
			if(ell_data.eContains(i, sol[i])) {
				//Valid subset: set $\hat{x}_{i,S} = \tilde{x}_{i,S}$
				key = ell_data.whichIndex(i, sol[i]);
			} else {
				//Find valid subset: $\hat{x}_{i,T'} = 1 for T' = \argmax_{T \subseteq S:(i,T) \in E} w_{i,T}$
				FloatingPoint max = FloatingPoint.NaN;
				//Iterate over all subset of player i and find most valuable using original wz
				for (Iterator<Integer> sets = ell_data.getItemsetIterator(i); sets.hasNext();) {
					Integer j = sets.next();
					if( ell_data.getItemsetHandler().isSubset(j, sol[i])
						&& (max.isNaN() || wz.get(ell_data.whichIndex(i, j)).compareTo(max) >= 0) ) {
						key = ell_data.whichIndex(i, j);
						max = wz.get(key);
					}
				}
				
				//No subset found
				if(key < 0) {
					log.warning("No subset for j="+sol[i]+" found");
					continue;
				}
				
				//DEBUG
				log.finer("Integer solution not in E, changed j=" + sol[i] + " to " + key + ".");
			}

			//Claim 3.2: Set $x^l_{i,S} = \hat{x}_{i,S} if w_{i,S} \geq 0$ and 0 otherwise
			if(wz.get(key).isPositive()) {
				x_l[key] = 1; //\hat{x} is either 0 or 1, and in this case it must be 1
			}
		}
		return x_l;
	}
	
	@Override
	public boolean lastWzWasAskedBefore() {
		return last_asked_before;
	}
	
	@Override
	public boolean lastWzWasNewSolution() {
		return last_solution_added;
	}
	
	@Override
	public void setObjectiveAsGamma(boolean deep_gamma) {
		use_objective_as_gamma = deep_gamma;
	}
}
