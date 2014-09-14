package tum.dss.thesis.knapsack;

import java.io.File;
import java.util.Locale;

import tum.dss.thesis.lsframework.AbstractMechanismData;
import tum.dss.thesis.lsframework.AbstractSimulation;

/**
 * Implementation of the automated simulation for the knapsack problem.
 * @author Florian Stallmann
 *
 */
public class Simulation extends AbstractSimulation {
	/**
	 * Main program to start the simulation.
	 * @param args command line arguments to setup simulation.
	 */
	public static void main(String[] args) {
		//Explanation of command line arguments
		if(args.length < 1 || args.length > 2) {
			System.err.println("Please provide the following command line arguments:");
			System.err.println("# simulation.jar sim_name [mode=auto]\n");
			System.err.println("sim_name := Name of simulation, a folder with this name will be created.");
			System.err.println("mode := auto (default)\n"
							 + "        Create model files in first run and solve in second run.\n\n"
							 + "        save-model-only\n"
							 + "        Only load or create data and Gurobi save model to file.\n\n"
							 + "        single-run\n"
							 + "        Create data, model and perform decomposition in a single run (uses files if already created).\n\n"
							 + "        solve-lp-only\n"
							 + "        Only solve LP Relaxation (first step) and terminate.\n\n"
							 + "        solve-lp-integral\n"
							 + "        Only solve unrelaxed LP to get optimal integer solution.\n\n"
							 );
			return;
		}
		
		Locale.setDefault(new Locale("en")); //use English for proper number notations
		configureLogging(); //read logging.properties file

		name = args[0]; //name of this simulation
		new File(name).mkdir(); //make sure folder exists
		
		mechanism = new Mechanism();
		mechanism.setIdenfitier(name);
		
		String mode = (args.length > 1 ? args[1] : "auto");

		// Create knapsack data if file does not already exist (delete files to create new data)
		if( !(new File(name + "/knp_data.lsdata.gz").isFile()) ) {
			KnapsackDataGenerator knpdata = new KnapsackDataGenerator(1000); //set to 100 items
			knpdata.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_DECREASING);
//			knpdata.generateNonSingleMindedData(500); //generate 200 players
			knpdata.generateSingleMindedData(1000);
//			knpdata.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_CONSTANT);
//			knpdata.generateKMindedData(10000, 2);
//			knpdata.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_DECREASING);
//			knpdata.generateKMindedData(500, 3);
//			knpdata.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_CONSTANT);
//			knpdata.generateKMindedData(500, 3);
//			knpdata.setValuationType(KnapsackDataGenerator.VAL_MARGINAL_INCREASING);
//			knpdata.generateKMindedData(500, 3);
			knpdata.saveToFile(name);
			mechanism.setData(knpdata); //Inject dataset
		} else {
			AbstractMechanismData knpdata = new KnapsackDataReader(name + "/knp_data.lsdata.gz");
			mechanism.setData(knpdata); //Inject dataset
		}
		
		//Either create and save model or load model if possible.
		boolean model_exists = new File(name + "/knp_gurobi_dual.lp").isFile();
		if(mode.equals("save-model-only") || (mode.equals("auto") && !model_exists)) {
			//Create model, save to file and end program
			((Mechanism) mechanism).createAndSaveModel(name);
			System.out.println("Model files created. Please re-run program in auto or single-run mode.");
			return;
		} else if( model_exists ) {
			//Load Model if file exists
			((Mechanism) mechanism).loadModel(name);
		}
		
		//Stop after solving LP just to see if result is interesting
		if(mode.equals("solve-lp-only")) {
			((Mechanism) mechanism).step1SolveLp();
			return;
		}
		
		//Try to solve LP integral to compare solution with relaxed solution
		if(mode.equals("solve-lp-integral")) {
			((Mechanism) mechanism).solveLpIntegral();
			return;
		}
		
		//mode == auto or single-run
		startSimulation();
	}
}
