package org.moten.david.util.math;

public class NewtonsMethodSolver {
	/**
	 * Returns a value of x that solves f=0 such that |f(x)|<=precision. Returns
	 * null if no solution found.
	 * 
	 * @param f
	 * @param xInitial
	 * @param h
	 * @param precision
	 * @return
	 */
	public static Double solve(Function<Double, Double> f, double xInitial,
			double h, double precision, long maxIterations) {
		// perform maxIterations iterations of Newton's method
		double x = xInitial;
		for (int i = 1; i <= maxIterations; i++) {
			double derivative = (f.apply(x + h) - f.apply(x)) / h;
			if (derivative == 0)
				return null;
			// Newton's method
			x = x - f.apply(x) / derivative;
			if (Math.abs(f.apply(x)) <= precision)
				return x;
		}
		if (Math.abs(f.apply(x)) <= precision)
			return x;
		else
			// not found
			return null;
	}
}
