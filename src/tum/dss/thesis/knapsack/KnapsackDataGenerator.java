package tum.dss.thesis.knapsack;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.FastMath;

import tum.dss.thesis.PlayerInterface;
import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Class to generate very simple knapsack data and save it to a file. Files can be read by {@link KnapsackDataReader}
 * @author Florian Stallmann
 *
 */
public class KnapsackDataGenerator extends AbstractMechanismData {
	private static final Logger log = Logger.getLogger( KnapsackDataGenerator.class.getName() );
	
	/** Constant for valuation vector with decreasing marginal value */
	public static final int VAL_MARGINAL_DECREASING = 0;
	/** Constant for valuation vector with constant marginal value */
	public static final int VAL_MARGINAL_CONSTANT   = 1;
	/** Constant for valuation vector with increasing marginal value */
	public static final int VAL_MARGINAL_INCREASING = 2;
	
	/** Type of valuation of generated players */
	protected int valuation_type = VAL_MARGINAL_CONSTANT;
	
	/** Data generator for different distributions */
	protected RandomDataGenerator rd = new RandomDataGenerator();
	
	/** Counter for player id */
	private int pl_count = 1;
	
	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = -4695770085382686223L;

	/**
	 * Initialize with number of items and players, generate data for single-minded players and save to file.
	 * @param items   number of items.
	 * @param players number of players.
	 */
	public KnapsackDataGenerator(final int items, final int players) {
		itemset_handler = new ItemsetHandler(items);
		
//		generateNonSingleMindedData(players);
		generateSingleMindedData(players);
//		generateKMindedData(players, 2);
		saveToFile("data");
	}
	
	/**
	 * Initializes with number of items only, no data generation yet.
	 * @param items number of items.
	 */
	public KnapsackDataGenerator(final int items) {
		itemset_handler = new ItemsetHandler(items);
	}
	
	/**
	 * Sets type of valuation for generated players.
	 * @param type valuation type.
	 */
	public void setValuationType(int type) {
		valuation_type = type;
	}
	
	/**
	 * Generates players with random single-minded (k=1) valuations.
	 * @param players number of players.
	 */
	public void generateSingleMindedData(int players) {
		generateKMindedData(players, 1);
//		int items = itemset_handler.getNumSubsets();
//		double[] val = new double[items+1];
//		int quantity = 0;
//		double valuation = 0;
//		ValuationGenerator vg = new ValuationGenerator(valuation_type);
//		
//		// Generate data using step function
//		for (int i = 0; i < players; i++) {
//			Arrays.fill(val, 0);
//			quantity = rd.nextInt(1, items); //uniform distr [1,items]
//			//valuation = quantity * FastMath.max(rd.nextGaussian(items/2, items/4), 1); //gauss distr with mu=100 and sigma=80;
//			valuation = vg.getValuation(quantity);
//			for (int j = quantity; j <= items; j++) {
//				val[j] = valuation;
//			}
//			addPlayer(new Player("Player (single-minded) " + (pl_count), val.clone()));
//			vg.nextPlayer();
//			pl_count++;
//		}
//
//		log.fine("Data generated: " + players + "x single-minded players.\n" + toString());
	}
	
	/**
	 * Generates players with random k-minded valuations.
	 * @param players number of players
	 * @param k       number of items with valuation (k-minded)
	 */
	public void generateKMindedData(int players, int k) {
		int items = itemset_handler.getNumSubsets();
		int[] quantities;
		ValuationGenerator vg = new ValuationGenerator(valuation_type);
		
		//Collection of all quantities
//		Collection<Integer> all_quant = new ArrayList<Integer>();
//		for (int j = 0; j <= items; j++) {
//			all_quant.add(j);
//		}
		
		// Generate data
		for (int i = 0; i < players; i++) {
			//New player
			PlayerInterface pl = new Player(String.format("P %5d [k=%d v=%d]", pl_count, k, valuation_type), k);
			pl.setItemsetHandler(itemset_handler);
			//Random sample of k quantities
//			quantities = rd.nextSample(all_quant, k);
			quantities = rd.nextPermutation(items, k); //return sample of size k from [0;items[
			Arrays.sort(quantities); //insert in order, probably not necessary but nice to have
			for (int j = 0; j < quantities.length; j++) {
				pl.setValuation(quantities[j]+1, vg.getValuation(quantities[j]+1));
			}
			addPlayer(pl);
			vg.nextPlayer();
			pl_count++;
		}
		
		log.fine("Data generated: " + players + "x " + k + "-minded players.\n" + toString());
	}
	
	/**
	 * Generates players with completely random valuations (k=items).
	 * @param players number of players.
	 */
	public void generateNonSingleMindedData(int players) {
		int items = itemset_handler.getNumSubsets();
		double[] val = new double[items+1];
		ValuationGenerator vg = new ValuationGenerator(valuation_type);
		
		// Generate data
		for (int i = 0; i < players; i++) {
			for (int j = 1; j <= items; j++) {
				val[j] = vg.getValuation(j);
			}
			addPlayer(new Player(String.format("P %d [k=m v=%d]", pl_count, valuation_type), val.clone()));
			vg.nextPlayer();
			pl_count++;
		}

		log.fine("Data generated: " + players + "x m-minded players.\n" + toString());
	}
	
	/**
	 * Saves this object with data to a compressed binary file knp_data.lsdata.gz
	 * and a compressed text file knp_data.txt.gz.
	 * @param folder folder name to create files in.
	 */
	public void saveToFile(String folder) {
//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//		String filename = folder + "/knp_data_" + timeStamp + ".";
		String filename = folder + "/knp_data.";
		
		// Save data to binary file for later use
		try {
			OutputStream saveFile = new GZIPOutputStream(new FileOutputStream(filename + "lsdata.gz"));
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(this);
			save.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Save data to text file
		try(
			PrintWriter out = new PrintWriter(
					new GZIPOutputStream(
					new BufferedOutputStream(
					new FileOutputStream(filename + "txt.gz") )))) {
		    out.println("m=" + getNumSubsets());
		    out.println("n=" + getNumPlayers());
			for (int i = 0; i < players.size(); i++) {
				out.println(getPlayer(i).toString());
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Simple class to generate different types of valuations.
	 * @author Florian Stallmann
	 *
	 */
	class ValuationGenerator {
		/** Valuation type, either 0, 1 or 2 */
		private int type;
		
		/** Randomized valuation for a single item */
		private double valuation;
		
		/** Randomized factor for vary marginal increase or decrease */
//		private double factor;
		
		/** Lower bound of valuation, this is log(number of items) */
		private double bound;
		
		/** Random data generator */
		private RandomDataGenerator rd;
		
		/**
		 * Initializes generator with type of valuation: 
		 * 0: decreasing, 1: constant, 2: increasing marginal value
		 * @param type valuation type, either 0, 1 or 2.
		 */
		public ValuationGenerator(int type) {
			if(type < 0 || type > 2) {
				throw new IllegalArgumentException("Invalid valuation type");
			}
			this.type = type;
			bound = FastMath.log(getNumSubsets()); //lower bound to valuation of single item
			rd = new RandomDataGenerator();
			//DEBUG
			log.finer("Valuation generator initialized with bound=" + bound);
			//initialize for first player
			nextPlayer();
		}
		
		/**
		 * Generates new random factors for next player.
		 */
		public void nextPlayer() {
//			factor = FastMath.log( rd.nextUniform(1, 2) ); //Marginal inc/dec factor to give some variation
			
			//the lower bound will prevent that...
			if(type == VAL_MARGINAL_DECREASING) {
				//..in case of marginal decreasing valuation the valuation might get negative.
				valuation = rd.nextUniform(bound+1, bound+11.0); //Value for single item
			} else if (type == VAL_MARGINAL_INCREASING) {
				//..in case of marginal increasing valuation the valuation might get too high.
				valuation = rd.nextUniform(1, bound-FastMath.log(2.0)); //Value for single item
			} else {
				valuation = rd.nextUniform(bound, 2*bound); //Value for single item
			}
			
			log.finer("Valuation generator updated with valuation=" + valuation);
		}
		
		/**
		 * Calculates value depending on given quantity.
		 * @param j quantity.
		 * @return  valuation.
		 */
		public double getValuation(int j) {
			if(type == VAL_MARGINAL_DECREASING) {
				return (valuation-FastMath.log(j)) * j;
//				return valuation + factor * j;
			} else if(type == VAL_MARGINAL_CONSTANT) {
				return (valuation * j);
//				return valuation + factor * ((j*j + j) / 2); //Gauss formula (1+2+3...)
			}
			//type == VAL_MARGINAL_INCREASING
			return (valuation+FastMath.log(j)) * j;
//			return valuation + factor * (j * (j+1) * (2*j+1)) / 6; //Gauss formula for quadr. (1^2+2^2+3^2...)
		}
	}

}
