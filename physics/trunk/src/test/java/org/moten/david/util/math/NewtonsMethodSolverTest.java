package org.moten.david.util.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NewtonsMethodSolverTest {

	@Test
	public void testSolveLinear() {
		Function<Double, Double> f = new Function<Double, Double>() {
			@Override
			public Double apply(Double x) {
				return 2 * x - 3;
			}
		};

		// should find a solution to a linear equation in 1 iteration
		double answer = NewtonsMethodSolver.solve(f, 4, 0.1, 0.0001, 1);
		assertEquals(1.5, answer, 0.000001);
	}

	@Test
	public void testSolveQuadratic() {
		Function<Double, Double> f = new Function<Double, Double>() {
			@Override
			public Double apply(Double x) {
				return 2 * x * x - 3 * x - 2;
			}
		};

		// should find a solution to a quadratic equation in 5 iterations
		double answer = NewtonsMethodSolver.solve(f, 4, 0.1, 0.0001, 5);
		assertEquals(2, answer, 0.0001);
	}

	@Test
	public void testSolveQuadraticStartingAtZeroDerivative() {
		Function<Double, Double> f = new Function<Double, Double>() {
			@Override
			public Double apply(Double x) {
				return x * x - 1;
			}
		};

		// should find a solution to a quadratic equation in 5 iterations
		Double answer = NewtonsMethodSolver.solve(f, 0, 0.1, 0.0001, 5);
		assertEquals(null, answer);
	}

	@Test
	public void testSolveQuadraticStartingLeftOfZeroDerivative() {
		Function<Double, Double> f = new Function<Double, Double>() {
			@Override
			public Double apply(Double x) {
				return x * x - 1;
			}
		};

		// should find a solution to a quadratic equation in 6 iterations
		double answer = NewtonsMethodSolver.solve(f, -0.1, 0.01, 0.0001, 6);
		assertEquals(-1, answer, 0.001);
	}

}
