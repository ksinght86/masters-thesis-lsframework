package tum.dss.thesis.lsframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Class to save all information about an integer solution including some analysis data.
 * @author Florian Stallmann
 *
 */
public class IntegerSolution {
	/** Iteration in which this solution was found */
	final protected int iteration;
	/** Integer solution itself */
	final protected int[] solution;
	
	/** Lambda after solving primal */
	protected double lambda = 0;
	/** Welfare/Objective value */
	protected double welfare = 0;
	/** Iterations in which duplicates of this solution where found */
	protected List<Integer> duplicates =  new ArrayList<Integer>();
	
	/** Dimension of the solution respectively length of solution array, used for static method to create csv header */
	protected static int dim = 0;
	
	/**
	 * Creates new integer solution.
	 * @param iteration iteration.
	 * @param solution  integer solution array.
	 */
	public IntegerSolution(final int iteration, final int[] solution) {
		this.iteration = iteration;
		this.solution = solution;
		//Set static variable dim of int array in first instance of this class
		if(dim == 0) {
			dim = solution.length;
		}
	}
	
	/**
	 * Sets lambda value of convex decomposition (from primal model).
	 * @param lambda lambda respectively probability.
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
	/**
	 * Adds iteration in which duplicate was found.
	 * @param iteration iteration.
	 */
	public void addDuplicate(final int iteration) {
		duplicates.add(iteration);
	}
	
	/**
	 * Gets this integer solution as array.
	 * @return array with integer solution.
	 */
	public int[] getSolution() {
		return solution;
	}
	
	/**
	 * Gets welfare respectively objective value of this solution.
	 * @return welfare.
	 */
	public double getWelfare() {
		return welfare;
	}
	
	/**
	 * Gets the lambda value of the primal model of the convex decomposition.
	 * <p>
	 * The lambda value is treated as probability that this integer solution occurs.
	 * @return lambda respectively probability.
	 */
	public double getLambda() {
		return lambda;
	}
	
	/**
	 * Calculates welfare / objective value of this solution.
	 * <p>
	 * The index in the valuation array maps to the index in the solution array.
	 * @param valuation valuation array
	 */
	public void calculateWelfare(double[] valuation) {
		//Validate length
		if(valuation.length != solution.length) {
			throw new IllegalArgumentException("Length of valuation array doesn't fit.");
		}
		
		//The following loop is equivalent to just summing up all valuation[i] where solution[i]==1
		welfare = 0;
		for (int i = 0; i < solution.length; i++) {
			welfare += solution[i] * valuation[i];
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof IntegerSolution)
				&& (
					(this == obj) 
					|| Arrays.equals(solution, ((IntegerSolution) obj).getSolution())
					);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(solution);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(solution);
	}
	
	/**
	 * Exports data of this solution to a row which can be saved to a CSV file.
	 * @param id identifier on the integer solution for the first row.
	 * @return   all data of this integer solution.
	 */
	public String toCsv(final int id) {
		String csv = "";
		String dupl = duplicates.toString().replace(",", "");
		String sol  = Arrays.toString(solution).replace(",", "");
		//Enforce English locale for numbers
		csv += String.format(new Locale("en") ,"%-14d %-14d %-14d %-14d %-14f %-14f %s %s", id, hashCode(), iteration, duplicates.size(), lambda, welfare, sol, dupl);
		return csv;
	}
	
	/**
	 * Writes CSV header which fits to the output of the <code>toCsv</code> method.
	 * @return csv header
	 */
	public static String csvHeader() {
		String csv = "";
		String sol_format = (dim == 0 ? "%s" : "%-" + (dim*2-1) + "s"); //length of solution is length/dim of array incl. space between numbers
		csv += String.format("%-14s %-14s %-14s %-14s %-14s %-14s ["+sol_format+"] [%s]", "no", "hash", "iter", "num_dupl", "lambda", "welfare", "solution", "dupl");
		return csv;
	}
}
