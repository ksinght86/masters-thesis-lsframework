package tum.dss.thesis.lsframework;

import java.io.Serializable;
import java.util.logging.Logger;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
import org.apache.commons.math3.util.OpenIntToDoubleHashMap.Iterator;

import tum.dss.thesis.ItemsetHandlerInterface;
import tum.dss.thesis.PlayerInterface;

/**
 * Abstract player which implements basic methods to set valuations and retrieve valuation using
 * an oracle access to maintain polynomial time.
 * @author Florian Stallmann
 *
 */
public abstract class AbstractPlayer implements Serializable, PlayerInterface {
	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = 6554640081270741537L;
	
	private static final Logger log = Logger.getLogger( AbstractPlayer.class.getName() );
	
	/** Name of player */
	protected final String name;
	
	/** Itemset handler */
	protected ItemsetHandlerInterface itemset_handler;
	
	/** Valuation vector (using special int{@literal ->}double hashmap) */
	protected OpenIntToDoubleHashMap valuation;
	
	/**
	 * Initializes player with name only.
	 * @param name name of player.
	 */
	public AbstractPlayer(final String name) {
		this.name = name;
		valuation = new OpenIntToDoubleHashMap();
	}
	
	/**
	 * Initializes player with name and complete valuation vector.
	 * @param name      name of player.
	 * @param valuation valuation vector as array.
	 */
	public AbstractPlayer(final String name, final double[] valuation) {
		this.name = name;
		setValuations(valuation);
	}
	
	/**
	 * Initializes player with name and number of item subsets.
	 * <p>
	 * Note that the number of subsets is used to initialize the hashmap for the
	 * valuations. So for a 2-minded player it is sufficient to pass 2 as the
	 * number of subsets.
	 * @param name    name of player.
	 * @param subsets number of subsets.
	 */
	public AbstractPlayer(final String name, final int subsets) {
		this.name = name;
		this.valuation = new OpenIntToDoubleHashMap(subsets);
	}
	
	@Override
	public void setItemsetHandler(final ItemsetHandlerInterface itemset) {
		this.itemset_handler = itemset;
	}
	
	@Override
	public double getValuation(final int subset) {
		//Found set, just return valuation
		if(valuation.containsKey(subset)) {
			return valuation.get(subset);
		}
		
		//Use oracle access for monotone valuations
		int set = findMostValuableSubset(subset);
		
		//If no subset found return 0
		if(set < 0) {
			log.finest(name + ": No valuation for set " + subset + " or its subsets found, return 0");
			return 0;
		}
		
		return valuation.get(set);
	}
	
	@Override
	public void setValuations(final double[] val) {
		valuation = new OpenIntToDoubleHashMap(val.length);
		for (int i = 0; i < val.length; i++) {
			valuation.put(i, val[i]);
		}
	}
	
	@Override
	public void setValuation(final int j, final double val) {
		if(val < 0) {
			throw new IllegalArgumentException("Valuation cannot be negative");
		}
		
		//Check if valuation in wz is monotone, so all subsets have lower or equal valuations
		int set = findMostValuableSubset(j);
		//Implicit check of claim 3.3 $\tilde{v}_i(S) = max_{T \subseteq S:(i,T)\in E} w_{i,T}^+$
		if(set >= 0 && valuation.get(set) > val) {
			//valuation of subset is higher, so skip this one (might happen because original wz is not monotone)
			log.info("Updating valuation of " + getName() + " skipped for j=" + j + ", val=" + val);
			return;
		}
		
		//Set valuation to given $w^+$
		valuation.put(j, val);
		
		//Claim 3.3: Monotonize valuation by updating supersets
		//Set $\tilde{v}_i(S) = max_{T \subseteq S:(i,T)\in E} w_{i,T}^+$
		//Iterate over all available valuations and check if is superset
		for (Iterator val_iter = valuation.iterator(); val_iter.hasNext();) {
			val_iter.advance(); //next element
			//if element is superset and value is lower, update valuation
			if( itemset_handler.isSuperset(val_iter.key(), j) && val_iter.value() < val ) {
				valuation.put(val_iter.key(), val);
			}
		}
	}
	
	/**
	 * Finds subset of j which has the highest value, returns -1 if no subset found.
	 * @param j index of set.
	 * @return  index of subset with highest valuation, otherwise -1 if not existent.
	 */
	protected int findMostValuableSubset(final int j) {
		//Claim 3.3: v^~_i(S) = max_{T<=S}:(i,T) in E w^+_i,T
		
		//variables to save best found solution
		double max = 0;
		int key = -1;
		
		//Iterate over all available valuations and check if it is a subset
		for (Iterator val = valuation.iterator(); val.hasNext();) {
			val.advance(); //next element
			//if element is subset of requested, find max value of all subsets and save key
			if( itemset_handler.isSubset(val.key(), j) && val.value() >= max ) {
				key = val.key();
				max = val.value();
			}
		}
		return key;
	}
	
	@Override
	public void dropOriginalValuations() {
		valuation = new OpenIntToDoubleHashMap();
	}
	
	@Override
	public void resetValuations() {
		//Iterate over all available valuations and set to 0
		for (Iterator val = valuation.iterator(); val.hasNext();) {
			val.advance(); //next element
			valuation.put(val.key(), 0);
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		String s = getName() + " values [";
		//Valuation must not be in order due to use of hashmap
		for (Iterator iterator = valuation.iterator(); iterator.hasNext();) {
			iterator.advance(); //next element
			s += String.format("%5s=%11.5f", itemset_handler.getSet(iterator.key()), iterator.value() );
			if(iterator.hasNext()) {
				s += ", ";
			}
		}
		return s + "]";
	}
}
