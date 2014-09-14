package tum.dss.thesis.lsframework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

import tum.dss.thesis.PrimalDecompositionInterface;

/**
 * Various helper methods for analyzing the ellipsoid method and write a bunch of files
 * for the analysis of the framework.
 * @author Florian Stallmann
 *
 */
public class AnalysisHelper {
	/** Instance of mechanism (also holds ellipsoid method data, lp, oracle) */
	public static AbstractMechanism instance_mechanism = null;
	/** Instance of ellipsoid method (to get iteration counter) */
	public static EllipsoidMethod instance_ell = null;
	/** Instance of primal to receive lambda values */
	public static PrimalDecompositionInterface instance_primal = null;

	/** Reason for ellipsoid termination (might be "reached N", or "threshold reached", or...) */
	public static String ell_end_reason;
	/** Last iteration of ellipsoid method */
	public static int ell_end_iter;
	
	/** Log iterations in which constraint cuts where performed */
	public static List<Integer> constraint_cuts =  new ArrayList<Integer>();
	
	/** Time measurement */
	public static long startTime, endTime;
	
	/** Deviation analysis: absolute deviation, variance, standard deviation, coefficient of variation */
	public static double abs_dev, var, std_dev, coeff_var;
	
	/** Save previously performed cut. This is only used for grouping in latex output file. */
//	private static String prev_cut = "";
	
	/**
	 * Initializes helper with given mechanism. Clears all previously saved data.
	 * @param mech Mechanism.
	 */
	public static void initializeHelper(AbstractMechanism mech) {
		instance_mechanism = mech;
		instance_ell = null;
		instance_primal = null;
		constraint_cuts.clear();
		abs_dev=0; var=0; std_dev=0;
		startTime=0; endTime=0;
		ell_end_iter=0; ell_end_reason="";
	}
	
	/**
	 * Logs the iteration of a constraint cut. This is equivalent to the number of calls on the approximation algorithm.
	 * @param i iteration.
	 */
	public static void logConstraintCut(int i) {
		constraint_cuts.add(i);
	}
	
	/**
	 * Saves iteration and reason for ellipsoid termination.
	 * @param i    iteration.
	 * @param type reason for termination.
	 */
	public static void logFinalIteration(int i, String type) {
		ell_end_iter = i;
		ell_end_reason = type;
	}
	
	/**
	 * Gets the data for histogram plots.
	 * <p>
	 * It will export the iterations of constraint cuts and the iterations in which
	 * integer solutions were found.
	 * @return string with histogram data.
	 */
	public static String getHistData() {
		String s = "";
		s += "Distribution of constraint cuts:\n"
				+ "cons_cuts="+constraint_cuts+";\n";
		
		List<Integer> int_sols = new ArrayList<Integer>( instance_mechanism.ell_data.getNumIntegerSolutions() );
		List<Integer> int_dupl = new ArrayList<Integer>( FastMath.max(1, constraint_cuts.size()-instance_mechanism.ell_data.getNumIntegerSolutions()) );
		for (int i = 0; i < instance_mechanism.ell_data.getNumIntegerSolutions(); i++) {
			int_sols.add( instance_mechanism.ell_data.getIntegerSolution(i).iteration );
			int_dupl.addAll( instance_mechanism.ell_data.getIntegerSolution(i).duplicates );
		}
		
		s += "Distribution of found integer solutions:\n"
				+ "int_sols="+int_sols.toString()+";\n";
		s += "Distribution of found duplicates:\n"
				+ "int_dupl="+int_dupl.toString()+";\n";
		
		return s;
	}
	
	/**
	 * Saves current time as start to measure any runtime.
	 */
	public static void startRuntimeMeasure() {
		startTime = System.nanoTime();
	}
	
	/**
	 * Saves current time as stop to measure any runtime.
	 */
	public static void stopRuntimeMeasure() {
		endTime = System.nanoTime();
	}
	
	/**
	 * Gets runtime in seconds by subtracting stop time from start time.
	 * @return runtime in seconds.
	 */
	public static double getRuntime() {
		return (endTime - startTime)/1e9;
	}
	
	/**
	 * Gets a summary of the ellipsoid method run.
	 * @return string with summary of ellipsoid method run.
	 */
	public static String getSummary() {
		//calculate the absolute deviation, variance and standard deviation by iterating over all integer solutions
		calculateDeviations();
		
		String s = "";
		s += String.format("Analysis results no. %d%n%n", instance_mechanism.counter);
		s += String.format("Ellipsoid method stopped in iteration %d because %s.%n", ell_end_iter, ell_end_reason);
		s += String.format("Ellipsoid parameters: cut=%s; eps=%f; N=%d; R=%f.%n", instance_ell.getCutName(), instance_ell.getEpsilon(), instance_ell.getMaxIterations(), instance_ell.getRadius());
		s += String.format("Ellipsoid performance: runtime=%f sec.; constraint-cuts=%d.%n%n", getRuntime(), constraint_cuts.size());
		s += String.format("First iteration in which primal was feasible: %d.%n", instance_ell.getFirstFeasibleIteration());
		s += String.format("In total %d integer solutions where found, whereas %d where used by primal.%n%n", instance_mechanism.ell_data.getNumIntegerSolutions(), instance_primal.getSolUsed().size());
		s += String.format("Solution quality:%nExpected social welfare=%f%nAbsolute deviation=%f%nVariance=%f%nStandard deviation=%f%nCoefficient of variation=%f%n", 
						instance_primal.getExpectedSocialWelfare(),
						abs_dev,
						var,
						std_dev,
						coeff_var);
		return s;
	}
	
	/**
	 * Calculates deviation measures of welfare of integer solutions from expected social welfare.
	 * <p>
	 * The following measures are calculated:
	 * <ul>
	 * <li>abs_dev: absolute deviation from expected social welfare
	 * <li>var: variance from expected social welfare
	 * <li>std_dev: standard deviation (square root of variance)
	 * <li>coeff_var: coefficient of variation (standard deviation divided by expected social welfare)
	 * </ul>
	 */
	public static void calculateDeviations() {
		if(abs_dev > 0) return; //already calculated
		
		double wel = instance_primal.getExpectedSocialWelfare();
		for (int i = 0; i < instance_mechanism.ell_data.getNumIntegerSolutions(); i++) {
			IntegerSolution xl = instance_mechanism.ell_data.getIntegerSolution(i);
			abs_dev += xl.getLambda() * Math.abs(xl.getWelfare() - wel);
			var += xl.getLambda() * Math.pow(xl.getWelfare() - wel, 2);
		}
		std_dev = Math.sqrt(var);
		coeff_var = std_dev / wel;
	}
	
	/**
	 * Saves analysis to a various files (text, csv, latex).
	 */
	public static void saveToFile() {
		String overview = instance_mechanism.id + "/analysis_overview.";
		
		//Data overview file [TXT] (created only once)
		if( !(new File(overview + "txt").isFile()) ) {
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(overview  +"txt")))) {
				out.println(instance_mechanism.toString());
				out.println(instance_mechanism.lp.toString());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//calculate the absolute deviation, variance and standard deviation by iterating over all integer solutions
		calculateDeviations();
		
		//Analysis overview [CSV] (APPEND!)
		boolean header = !(new File(overview + "csv").isFile());
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(overview  +"csv", true)))) {
			if(header) out.println( String.format("%-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s %-16s", "id", "cut", "feas_iter", "end_iter", "runtime", "num_conscuts", "num_sol", "used_sol", "opt_welfare", "alpha", "exp_welfare", "efficiency", "abs_dev", "var", "std_dev", "coeff_var", "decomp_obj") );
			out.println(String.format(new Locale("en"),
					"%-16d %-16s %-16d %-16d %-16f %-16d %-16d %-16d %-16f %-16f %-16f %-16f %-16f %-16f %-16f %-16f %-16f",
						instance_mechanism.counter,
						instance_ell.getCutName(),
						instance_ell.getFirstFeasibleIteration(),
						ell_end_iter,
						getRuntime(),
						constraint_cuts.size(),
						instance_mechanism.ell_data.getNumIntegerSolutions(),
						instance_primal.getSolUsed().size(),
						instance_mechanism.lp.getObjective(),
						(instance_mechanism.overwrite_int_gap > 0 ? instance_mechanism.overwrite_int_gap : instance_mechanism.oracle.getIntegralityGap()),
						instance_primal.getExpectedSocialWelfare(),
						(1.0 * instance_mechanism.ell_data.getNumIntegerSolutions() / constraint_cuts.size()),
						abs_dev,
						var,
						std_dev,
						coeff_var,
						instance_primal.getObjective()
						));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		//Analysis overview [Latex Tabular Code for my thesis] (APPEND!)
//		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(overview + "tex", true)))) {
//			if( !prev_cut.equals(instance_ell.getCutName()) ) {
//				out.println("\\addlinespace");
//			}
//			String first_col =  (instance_mechanism.counter == 1 ? "\\textsf{" + instance_mechanism.id + "}" : 
//								(instance_mechanism.counter == 2 ? String.format("$\\overline{W}=%5.3f$", instance_primal.getExpectedSocialWelfare()) : 
//								(instance_mechanism.counter == 3 ? "$|E|="+(instance_mechanism.ell_data.getDimension()-1)+"$" : "")));
//			out.println(String.format(new Locale("en"),
//					"%-25s & %-12s & %-10d & %-10.3f & %-10d & %-10d & %-10d & %-10.3f & %-10.3f \\\\",
//						first_col,
//						(!prev_cut.equals(instance_ell.getCutName()) ? instance_ell.getCutName() :  ""),
//						//(prev_cut != instance_ell.getCutName() ? Integer.toString(instance_ell.getFirstFeasibleIteration()) : ""),
//						ell_end_iter,
//						getRuntime(),
//						constraint_cuts.size(),
//						instance_mechanism.ell_data.getNumIntegerSolutions(),
//						instance_primal.getSolUsed().size(),
//						(100.0 * instance_mechanism.ell_data.getNumIntegerSolutions() / constraint_cuts.size()),
//						abs_dev
//						));
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		prev_cut = instance_ell.getCutName();
		
		//Individual files
		String filename = instance_mechanism.id + "/" + "analysis_" + instance_mechanism.counter + ".";
		
		//Text file with summary (OVERWRITE!)
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename + "txt")))) {
		    out.println(getSummary());
		    out.println(getHistData());
		    out.println(instance_primal.toString());
		    out.println('\n');
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//CSV File with solutions (OVERWRITE!)
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename + "csv")))) {
		    out.println(IntegerSolution.csvHeader());
			for (int i = 0; i < instance_mechanism.ell_data.getNumIntegerSolutions(); i++) {
				out.println( instance_mechanism.ell_data.getIntegerSolution(i).toCsv(i+1) );
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
