package tum.dss.thesis.knapsack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import tum.dss.thesis.lsframework.AbstractMechanismData;

/**
 * Class to read previously saved data using {@link KnapsackDataGenerator}.
 * @author Florian Stallmann
 *
 */
public class KnapsackDataReader extends AbstractMechanismData {
	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = -6403470928794197090L;

	/**
	 * Initializes with filename (with folder) and read data from file.
	 * @param filename filename to read.
	 */
	public KnapsackDataReader(final String filename) {
		try {
			InputStream saveFile = new GZIPInputStream(new FileInputStream(filename));
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			KnapsackDataGenerator obj = (KnapsackDataGenerator) restore.readObject();
			restore.close();
			
			this.itemset_handler = obj.itemset_handler;
			this.players = obj.players;
			//DEBUG:
			System.out.println("Data loaded from file:\n" + this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
