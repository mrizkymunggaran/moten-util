package org.moten.david.physics.fluids;

import org.moten.david.util.math.Function;
import org.moten.david.util.math.NewtonsMethodSolver;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

/**
 * Solver for Newtonian incompressible fluids, in particular seawater. Based on
 * <a
 * href="http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations">this</a>.
 * 
 * @author dxm
 * 
 */
public class NavierStokesSolver {

	private static final long MAX_NEWTONS_ITERATIONS = 20;
	private static Vector g = new Vector(0, 0, -9.8); // in metres/second
														// squared

	/**
	 * Returns a copy of {@link Data} after timeDelta.
	 * 
	 * @param data
	 * @param timeDelta
	 * @return
	 */
	public Data getDataAfterTime(Data data, double timeDelta) {
		Data data2 = data.copy();
		for (Pair<Vector, Value> pair : data.getValues()) {
			Value value = getValueAfterTime(data, pair.getA(), timeDelta);
			data2.setValue(pair.getA(), value);
		}
		return data2;
	}

	/**
	 * Returns the derivative of the velocity vector with time.
	 * 
	 * @param data
	 * @param position
	 * @return
	 */
	private Vector getVelocityDerivativeWithTime(Data data, Vector position) {
		Value value = data.getValue(position);
		return data
				.getVelocityLaplacian(position)
				.multiply(value.viscosity)
				.minus(data.getPressureGradient(position))
				.divide(value.density)
				.add(g)
				.minus(data.getVelocityJacobian(position).multiply(
						value.velocity));
	}

	/**
	 * Returns the velocity vector estimate after time <code>timeDelta</code>.
	 * The returned vector may not satisfy the Continuity equation and so may
	 * require pressure correction and recalculation of velocity for an
	 * acceptable result.
	 * 
	 * @param position
	 * @param velocity
	 * @param pressure
	 * @param timeDelta
	 * @param differentiator
	 * @param data
	 * @param stepHint
	 * @return
	 */

	private Vector getVelocityAfterTime(Data data, Vector position,
			double timeDelta) {
		Vector dVdt = getVelocityDerivativeWithTime(data, position);
		Value value = data.getValue(position);
		return value.velocity.add(dVdt.multiply(timeDelta)).add(value.velocity);
	}

	/**
	 * Calculate velocity and pressure after timeDelta has passed.
	 * 
	 * @param position
	 * @param timeDelta
	 * @param differentiator
	 * @param data
	 * @param stepHint
	 * @return
	 */
	private Value getValueAfterTime(Data data, Vector position, double timeDelta) {
		Value value0 = data.getValue(position);
		Vector v0 = value0.velocity;
		double p0 = value0.pressure;

		Vector v1 = getVelocityAfterTime(data, position, timeDelta);
		// if stopped now then continuity (conservation of mass) equation might
		// not be satisfied.
		//
		// Perform pressure correction as per
		// http://en.wikipedia.org/wiki/Pressure-correction_method
		//
		// i.e. solve f = 0Value for pressure p where f is defined:
		//
		// Take the value of velocity v1 just created and find pressure s.t
		// du/dx + dv/dy + dw/dz = 0
		Function<Double, Double> f = createContinuityFunction(data, position,
				v1, timeDelta);

		// TODO best value for precision?
		double precision = 0.001;
		// TODO best value for step size?
		double pressureStepSize = 1; // in Pa
		Double pressure = NewtonsMethodSolver.solve(f, p0, pressureStepSize,
				precision, MAX_NEWTONS_ITERATIONS);
		if (pressure == null)
			pressure = p0;

		return new Value(v1, pressure, value0.depth, value0.density,
				value0.viscosity);
	}

	/**
	 * Returns the Conservation of Mass (Continuity) Equation described by the
	 * Navier-Stokes equations.
	 * 
	 * @param position
	 * @param velocity
	 * @param timeDelta
	 * @param differentiator
	 * @param data
	 * @param stepHint
	 * @return
	 */
	private Function<Double, Double> createContinuityFunction(final Data data,
			final Vector position, final Vector v1, final double timeDelta) {
		return new Function<Double, Double>() {
			@Override
			public Double apply(Double p) {
				Vector velocityNext = getVelocityAfterTime(data, position,
						timeDelta);
				Value v = data.getValue(position);
				Value valueNext = new Value(velocityNext, v.pressure, v.depth,
						v.density, v.viscosity);
				return divergence(new DataOverride(data, position, valueNext),
						position);
			}
		};
	}

	/**
	 * Returns the value of the Divergence operator at the given position. See
	 * <a href="http://en.wikipedia.org/wiki/Del">here</a>.
	 * 
	 * @param position
	 * @param velocity
	 * @param differentiator
	 * @param data
	 * @param stepHint
	 * @return
	 */
	private static double divergence(Data data, Vector position) {
		return data.getVelocityJacobian(position).diagonalSum();
	}
}
