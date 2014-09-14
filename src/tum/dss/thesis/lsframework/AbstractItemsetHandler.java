package tum.dss.thesis.lsframework;

import java.io.Serializable;

import tum.dss.thesis.ItemsetHandlerInterface;

/**
 * Abstract class to handle basic operations on itemsets.
 * @author Florian Stallmann
 *
 */
public abstract class AbstractItemsetHandler implements Serializable, ItemsetHandlerInterface {
	/**
	 * Needs to be set to save object into file.
	 */
	private static final long serialVersionUID = 5850508247929433088L;

}
