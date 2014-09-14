package tum.dss.thesis.lsframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.SparseVector;
import org.jscience.mathematics.vector.Vector;

import tum.dss.thesis.LinearProgramInterface;
import tum.dss.thesis.MatrixHelper;
import tum.dss.thesis.MechanismDataInterface;
import tum.dss.thesis.PlayerInterface;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

/**
 * Functions to handle the data used for the ellipsoid method, i.e. the mapping of different data structures.
 * @author Florian Stallmann
 *
 */
public class EllipsoidMethodData extends AbstractMechanismData {
	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = -6999351208258425280L;
	
	private static final Logger log = Logger.getLogger( EllipsoidMethodData.class.getName() );
	
	/** Original mechanism data which was solved by Gurobi */
	protected MechanismDataInterface original_data;
	
	/** List of k items (index same as in x<sup>*</sup>) with pair (index of player, index of subset) */
	protected List<int[]> E = new ArrayList<int[]>();
	
	/** Data structure to map player and item/subset to index in x<sup>*</sup> */
	protected Table<Integer, Integer, Integer> E_table = HashBasedTable.create();
	
	/** LP-solution, index maps to index in list E */
	protected double[] x_star;
	
	/** Holds original valuations of LP solution to calculate welfare of integer solutions */
	protected double[] x_valuations;
	
	/** Integer solutions x<sup>l</sup> */
	protected List<IntegerSolution> x_l;
	
	/** Vector equal to x<sup>*</sup> plus additional entry of 0 to speed up objective value calculation */
	private Vector<FloatingPoint> x_star_z;
	
	/**
	 * Initializes data without original mechanism data.
	 */
	public EllipsoidMethodData() {
		clearSolutions();
	}
	
	/**
	 * Initializes data with original mechanism data used for LP.
	 * @param data original mechanism data.
	 */
	public EllipsoidMethodData(final MechanismDataInterface data) {
		this();
		setData(data);
	}
	
	/**
	 * Dependency injection: set data.
	 * @param data original mechanism data.
	 */
	public void setData(final MechanismDataInterface data) {
		original_data = data;
		itemset_handler = data.getItemsetHandler();
	}
	
	/**
	 * Removes all integer solutions for new mechanism run. 
	 */
	public void clearSolutions() {
		x_l = new ArrayList<IntegerSolution>();
	}
	
	/**
	 * Imports LP result by saving allocation and drop existing valuations.
	 * @param lp model with result of linear program solved using any solver.
	 */
	public void importLPResults(final LinearProgramInterface lp) {
		int i_new = 0; //counter for player index
		int k = 0; //counter for x_star index
		Multimap<Integer, Integer> sol_used = lp.getSolUsed(); //indices of used solutions
		
		x_star       = new double[sol_used.size()]; //initialize x_star
		x_valuations = new double[sol_used.size()]; //initialize x_valuations
		
		//Iterate over all players
		for (Iterator<Integer> players = sol_used.keySet().iterator(); players.hasNext();) {
			Integer i = players.next();
			PlayerInterface pl = original_data.getPlayer(i); //get original player
			
			//Iterate over all possible subsets (1): mapping, save x_star and valuations for welfare
			for (Iterator<Integer> sets = sol_used.get(i).iterator(); sets.hasNext();) {
				Integer j = sets.next();
				
				addMapping(k, i_new, j); //add mapping of indices (using map and list)
				
				x_star[k] = lp.getResult(i,j); //linear representation of LP solution
				x_valuations[k] = pl.getValuation(j); //save original valuation
				k++;
			}
			
			pl.dropOriginalValuations(); //remove all valuations as they are not needed anymore
			
			//Iterate over all possible subsets (2): set valid subsets' valuation to 0
			for (Iterator<Integer> sets = sol_used.get(i).iterator(); sets.hasNext();) {
				Integer j = sets.next();
				pl.setValuation(j, 0); //set valuation to 0 for all valid subsets
			}
			
			addPlayer(pl); //add player
			i_new++; //increase player counter
		}
		
		//DEBUG: output x_star
		log.info("LP import, set x* = " + Arrays.toString(x_star));
		//DEBUG: output mechanism data
		log.info(this.toString());
	}
	
	/**
	 * Gets initial center with w<sub>i,S</sub>=0 and z=1.
	 * <p>
	 * See proof of lemma 3.4 paragraph 1 for explanation.
	 * @return vector with initial center.
	 */
	public Vector<FloatingPoint> getInitialCenter() {
		return getInitialCenter(0);
	}
	
	/**
	 * Gets initial center of given type.
	 * <p>
	 * The original paper suggests to use w<sub>i,S</sub>=0 and z=1 as center.
	 * This method returns the center by given type. The following types
	 * are available:
	 * <ul>
	 * <li>0: original center as defined above
	 * <li>1: vector consisting of 0s
	 * <li>2: vector consisting of 1s
	 * <li>3: vector with w<sub>i,S</sub>=1 and z=0
	 * </ul>
	 * @param type type of center, either 0, 1 or 2 as defined above.
	 * @return     vector with initial center.
	 */
	public Vector<FloatingPoint> getInitialCenter(int type) {
		DenseVector<FloatingPoint> wz;
		if(type == 1) {
			wz = DenseVector.valueOf(SparseVector.valueOf(getDimension(), FloatingPoint.ZERO, 0, FloatingPoint.ZERO));
		} else if(type == 2) {
			wz = DenseVector.valueOf(SparseVector.valueOf(getDimension(), FloatingPoint.ONE, 0, FloatingPoint.ONE));
		} else if(type == 3) {
			wz = DenseVector.valueOf(SparseVector.valueOf(getDimension(), FloatingPoint.ONE, getDimension()-1, FloatingPoint.ZERO));
		} else {
			//Create sparse matrix with last entry z=1
			wz = DenseVector.valueOf(SparseVector.valueOf(getDimension(), FloatingPoint.ZERO, getDimension()-1, FloatingPoint.ONE));
		}
		return wz;
	}
	
	/**
	 * Calculates objective value given current center.
	 * <p>
	 * See proof of lemma 3.4 paragraph 2 for explanation.
	 * @param wz              current center.
	 * @param integrality_gap integrality gap of separation oracle.
	 * @return                objective value.
	 */
	public FloatingPoint getObjectiveValue(final Vector<FloatingPoint> wz, final double integrality_gap) {
		FloatingPoint sum = FloatingPoint.ZERO;
		
		//The old manual way:
//		// $obj = sum of x_star * w$  ...
//		for (int i = 0; i < getDimension()-1; i++) {
//			sum = sum.plus(wz.get(i).times(FloatingPoint.valueOf(x_star[i])));
//		}
//		// ... $* 1/alpha + z$
//		sum = sum.divide(FloatingPoint.valueOf(integrality_gap)).plus(wz.get(getDimension()-1));
		
		//Calculate x_star_z on first call for faster calculation in the future
		if(x_star_z == null) {
			double[] x_star_z_tmp = new double[getDimension()];
			for (int i = 0; i < getDimension()-1; i++) {
				x_star_z_tmp[i] = x_star[i] / integrality_gap;
			}
			x_star_z_tmp[getDimension()-1] = 0;
			x_star_z = MatrixHelper.createVector(x_star_z_tmp);
		}
		
		sum = wz.times(x_star_z).plus(wz.get(getDimension()-1));
		
		//DEBUG: Output objective value
		log.fine("Objective value calculated: " + sum);
		
		return sum;
	}
	
	/**
	 * Updates player valuations with given wz vector and monotonize valuations.
	 * <p>
	 * Using:
	 * <ul>
	 * <li>Claim 3.2: handle negative valuations by setting w<sup>+</sup> = max(w,0)
	 * <li>Claim 3.3: set valuation to monotone version of w<sup>+</sup> (performed in {@link AbstractPlayer#setValuation(int, double)})
	 * </ul>
	 * @param wz current center.
	 */
	public void updatePlayerValuation(final Vector<FloatingPoint> wz) {
		//Instead of iterating over all entries in wz it is easier to iterate over set E
		//Iterate over players and corresponding subsets
		for (Iterator<Integer> players = getPlayerIterator(); players.hasNext();) {
			Integer i = players.next();
			PlayerInterface pl = getPlayer(i);
			pl.resetValuations(); //reset valuation to 0 each time
			
			//Iterate over subsets of player
			for (Iterator<Integer> sets = getItemsetIterator(i); sets.hasNext();) {
				Integer j = sets.next();
				double val = wz.get(whichIndex(i, j)).doubleValue(); //get valuation in wz
				//Claim 3.2: Set valuation to $w^+ = max(w_{i,S};0)$
				if(val > 0) { //all other valuations are 0 by default
					pl.setValuation(j, val); //monotonization handled by player
				}
			}
		}
		//DEBUG: output mechanism data
		log.finer("Player valuation updated:\n" + this.toString());
	}
	
	/**
	 * Performs duplicate check and saves integer solution to x<sup>l</sup>.
	 * @param int_sol   found and already translated integral solution.
	 * @param iteration current ellipsoid iteration.
	 * @return          true if solution has not been added before, otherwise false.
	 */
	public boolean addIntegerSolution(final int[] int_sol, final int iteration) {
		//Validate length
		if(int_sol.length != getDimension()-1) {
			throw new IllegalArgumentException("Length of integer solution array doesn't fit.");
		}
		
		IntegerSolution sol = new IntegerSolution(iteration, int_sol); //Create new integer solution

		//Duplicate check (see equals method in IntegerSolution class)
		int duplicate = x_l.indexOf(sol);
		if(duplicate >= 0) {
			x_l.get(duplicate).addDuplicate(iteration);
			log.finer("Duplicate integral solution found: " + sol);
			return false;
		}
		
		//If import was performed for valuations calculate welfare (doesn't happen, e.g., in unit tests)
		if(x_valuations != null) {
			sol.calculateWelfare(x_valuations);
		}
		x_l.add(sol); //Add new solution
		
		//DEBUG: new inequality
		log.info("New integral solution added: " + sol);
		return true;
	}
	
	/**
	 * Add all integer solutions manually that are valid either
	 * due to the packing property or because only a single assignment will be made.
	 * <p>
	 * Example: Lets assume x<sup>*</sup> = (1 0.5 1 1). The packing property allows us to add
	 * x<sup>l</sup> = (1 0 1 1) as solution. Moreover, we can assign the single fractional
	 * solution and add x<sup>l</sup> = (0 1 0 0). The zero vector (0 0 0 0) can be added
	 * as it is always valid.
	 * @param add_zero true to add the zero vector as solution which is always valid.
	 */
	public void addManualIntegerSolutions(final boolean add_zero) {
		if(add_zero) {
			addIntegerSolution(new int[getDimension()-1], 0);
		}
		int[] pp_sol = new int[getDimension()-1];
		for (int i = 0; i < getDimension()-1; i++) {
			if(x_star[i] == 1.0) {
				pp_sol[i] = 1;
			} else {
				int[] frac_sol = new int[getDimension()-1];
				frac_sol[i] = 1;
				addIntegerSolution(frac_sol, 0);
			}
		}
		addIntegerSolution(pp_sol, 0);
	}
	
	/**
	 * Gets the total number of found integer solutions.
	 * @return number of integer solutions.
	 */
	public int getNumIntegerSolutions() {
		return x_l.size();
	}
	
	/**
	 * Gets an integer solution by index.
	 * @param i index of solution.
	 * @return  integer array of solution.
	 */
	public IntegerSolution getIntegerSolution(int i) {
		return x_l.get(i);
	}
	
	/**
	 * Will always return the size of x<sup>*</sup>+1 = E.size+1 = wz.length.
	 * <p>
	 * This method was introduced to avoid any confusions when iterating over different variables
	 * which actually have the same dimension. It should be preferred for all loops if possible.
	 * @return dimension
	 */
	public int getDimension() {
		return x_star.length+1;
	}
	
	/**
	 * Which player index belongs to index k in set E.
	 * @param k index in set E.
	 * @return  index of player.
	 */
	public int whichPlayer(final int k) {
		return E.get(k)[0];
	}
	
	/**
	 * Which subset index belongs to index k in set E.
	 * @param k index in set E.
	 * @return  index of item subset.
	 */
	public int whichItemSubset(final int k) {
		return E.get(k)[1];
	}
	
	/**
	 * Which index in x<sup>*</sup> belongs to player i and subset j.
	 * @param i index of player.
	 * @param j index of subset.
	 * @return  index in set E.
	 */
	public int whichIndex(final int i, final int j) {
		return E_table.get(i, j);
	}
	
	/**
	 * Checks if set E contains pair (i,j).
	 * @param i index of player.
	 * @param j index of subset.
	 * @return  true if set E contains pair (i,j).
	 */
	public boolean eContains(final int i, final int j) {
		return E_table.contains(i, j);
	}
	
	/**
	 * Gets iterator over all players in E.
	 * @return iterator over players.
	 */
	public Iterator<Integer> getPlayerIterator() {
		return E_table.rowKeySet().iterator();
	}
	
	/**
	 * Gets iterator over all items of player i in E.
	 * @param i index of player.
	 * @return  iterator over all itemsets of player i.
	 */
	public Iterator<Integer> getItemsetIterator(final int i) {
		return E_table.row(i).keySet().iterator();
	}
	
	/**
	 * Add mapping of index in x<sup>*</sup> to player and subset.
	 * @param k index in x<sup>*</sup>.
	 * @param i index of player.
	 * @param j index of subset.
	 */
	public void addMapping(int k, int i, int j) {
		E.add(k, new int[] {i, j}); //i (=i_new) will become new index of player i (in case a player was skipped because LP allocates nothing to him)
		E_table.put(i, j, k); //create map E_table with player index and quantity as key to index in x_star as value
	}
}
