package org.moten.david.util.math;

/**
 * Solves zeros of a univariate function using Newton's Method.
 * 
 * @author dave
 * 
 */
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
		double fx = f.apply(x);
		int i = 1;
		while (i <= maxIterations && Math.abs(fx) > precision) {
			double derivative = (f.apply(x + h) - fx) / h;
			if (derivative == 0)
				return null;
			// Newton's method
			x = x - fx / derivative;
			fx = f.apply(x);
			i++;
		}
		if (Math.abs(fx) <= precision)
			return x;
		else
			// not found
			return null;
	}
}
