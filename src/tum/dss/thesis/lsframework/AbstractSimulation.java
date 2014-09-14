package tum.dss.thesis.lsframework;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Abstract simulation class to run multiple instances of a mechanism with different parameters.
 * This class provides methods to setup a logger and to read a file which setups the simulation.
 * @author Florian Stallmann
 * 
 */
public abstract class AbstractSimulation {
	/** Instance of mechanism */
	protected static AbstractMechanism mechanism;
	
	/** Name of simulation, used as folder to save files */
	protected static String name;
	
	/**
	 * Loads the settings for logging from logging.properties file. Should be called by main method.
	 */
	public static void configureLogging() {
		//Settings for log handler
		System.setProperty( "java.util.logging.config.file", "logging.properties" );
		try { LogManager.getLogManager().readConfiguration(); Logger.getGlobal().info("Log initiated!"); }
		catch ( Exception e ) { e.printStackTrace(); }
	}
	
	/**
	 * Reads the simulation.properties file and runs the iterations of the mechanism. Should be called by main method.
	 * <p>
	 * The simulation.properties file for two iterations of the mechanism would look as follows:
	 * <code>
	 * default.eps = 0.001
	 * a1 = true
	 * a1.iterations = 100
	 * a2 = true
	 * a2.iterations = 1000
	 * </code>
	 */
	public static void startSimulation() {
		try {
			//Create example simulation.properties file if not exists
			if( !(new File(name + "/simulation.properties").isFile()) ) {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(name + "/simulation.properties")));
				out.println("### Example simulation file ###");
				out.println("#Parameters for all runs");
				out.println("default.cut = central");
				out.println("default.eps = 0.000001");
				out.println("default.iterations = 500000");
				out.println("default.stopIfFeasible = false");
				out.println("default.manualIntegerSolutions = false");
				out.println("default.objectiveAsGamma = false");
				out.println("default.initialCenterType = 0");
				out.println("default.overwriteGap = 0");
				out.println("default.minimizeDeviation = false");
				out.println();
				out.println("#Analysis 1");
				out.println("a1 = true");
				out.println("a1.stopIfFeasible = true");
				out.println();
				out.println("#Analysis 2");
				out.println("a2 = true");
				out.println("a2.iterations = 100");
				out.close();
			}
			
			//Read simulation.properties file with settings for analysis
			Properties properties = new Properties();
			BufferedInputStream stream;
			stream = new BufferedInputStream(new FileInputStream(name + "/simulation.properties"));
			properties.load(stream);
			stream.close();
			
			int i = 1;
			//Iterate as long as a1, a2, ai... property exists
			while(properties.getProperty("a"+i) != null) {
				//Skip if ai = false
				if( Boolean.parseBoolean(properties.getProperty("a"+i)) ) {
					//Set default properties each iteration
					setProperties(properties, "default.");
					//Update all defined properties
					setProperties(properties, "a"+i+".");
					//Run mechanism
					mechanism.runMechanism();
				} else {
					//Skip counter for proper filenames
					mechanism.counter++;
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if any of the known properties is set in properties object and calls corresponding method on mechanism.
	 * @param props  properties object read from file.
	 * @param prefix prefix of property names, like <code>a1.</code>.
	 */
	protected static void setProperties(Properties props, String prefix) {
		if(props.getProperty(prefix + "eps") != null) {
			mechanism.setEpsilon( Double.parseDouble(props.getProperty(prefix + "eps")) );
		}
		if(props.getProperty(prefix + "cut") != null) {
			String cut = props.getProperty(prefix + "cut");
			if(cut.equals("central")) {
				mechanism.setCut(EllipsoidMethod.CUT_CENTRAL);
			} else if(cut.equals("deep")) {
				mechanism.setCut(EllipsoidMethod.CUT_DEEP);
			} else if(cut.equals("shallow")) {
				mechanism.setCut(EllipsoidMethod.CUT_SHALLOW);
			}
		}
		if(props.getProperty(prefix + "stopIfFeasible") != null) {
			mechanism.setStopIfFeasible( Boolean.parseBoolean(props.getProperty(prefix + "stopIfFeasible")) );
		}
		if(props.getProperty(prefix + "manualIntegerSolutions") != null) {
			mechanism.setManualIntegerSolutions( Boolean.parseBoolean(props.getProperty(prefix + "manualIntegerSolutions")) );
		}
		if(props.getProperty(prefix + "objectiveAsGamma") != null) {
			mechanism.setObjectiveAsGamma( Boolean.parseBoolean(props.getProperty(prefix + "objectiveAsGamma")) );
		}
		if(props.getProperty(prefix + "initialCenterType") != null) {
			mechanism.setInitialCenter( Integer.parseInt(props.getProperty(prefix + "initialCenterType")) );
		}
		if(props.getProperty(prefix + "overwriteGap") != null) {
			mechanism.setOverwriteGap( Double.parseDouble(props.getProperty(prefix + "overwriteGap")) );
		}
		if(props.getProperty(prefix + "iterations") != null) {
			mechanism.setMaxIterations( Integer.parseInt(props.getProperty(prefix + "iterations")) );
		}
		if(props.getProperty(prefix + "radius") != null) {
			mechanism.setRadius( Double.parseDouble(props.getProperty(prefix + "radius")) );
		}
		if(props.getProperty(prefix + "minimizeDeviation") != null) {
			mechanism.setMinimizeDeviation( Boolean.parseBoolean(props.getProperty(prefix + "minimizeDeviation")) );
		}
	}
}
