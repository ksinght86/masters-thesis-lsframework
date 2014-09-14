package tum.dss.thesis.knapsack;

import tum.dss.thesis.LinearProgramInterface;
import tum.dss.thesis.SeparationOracleInterface;
import tum.dss.thesis.lsframework.AbstractMechanism;

/**
 * Implementation of whole LS Framework with different steps and analysis.
 * @author Florian Stallmann
 *
 */
public class Mechanism extends AbstractMechanism {
	
	/**
	 * Initializes Gurobi model for knapsack problem.
	 */
	public Mechanism() {
		super();
	}
	
	/**
	 * Creates Gurobi dual model and saves model to file.
	 * @param folder folder name to create file in.
	 */
	public void createAndSaveModel(String folder) {
		GurobiModelDual dual = ((GurobiModelDual) lp);
		dual.createLP();
		dual.saveToFile(folder);
	}
	
	/**
	 * Loads Gurobi dual model from file.
	 * @param folder folder name to read file from.
	 */
	public void loadModel(String folder) {
		GurobiModelDual dual = ((GurobiModelDual) lp);
		dual.readFromFile(folder);
	}

	@Override
	public SeparationOracleInterface createOracle() {
		return new SeparationOracle();
	}
	
	@Override
	public void solveLpIntegral() {
		//integral cannot be achivied by dual, so replace with original LP
		lp = new GurobiModel();
		setData(this.data);
		
		super.solveLpIntegral();
		
		//restore dual
		lp = new GurobiModelDual();
		setData(this.data);
	}

	@Override
	public LinearProgramInterface createLP() {
		return new GurobiModelDual();
	}
}
