package org.moten.david.physics.fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.moten.david.util.math.Function;
import org.moten.david.util.math.Matrix;
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

	private static Logger log = Logger.getLogger(NavierStokesSolver.class
			.getName());

	private static final long MAX_NEWTONS_ITERATIONS = 20;

	// in
	// metres/second

	// squared

	/**
	 * Returns a copy of {@link Data} after timeDelta.
	 * 
	 * @param data
	 * @param timeDelta
	 * @return
	 */
	public Data getDataAfterTime(Data data, double timeDelta) {
		List<Pair<Vector, Value>> list = new ArrayList<Pair<Vector, Value>>();
		Map<Vector, Value> map = new TreeMap<Vector, Value>();
		for (Pair<Vector, Value> pair : data.getEntries()) {
			Value value = getValueAfterTime(data, pair.get1(), timeDelta);
			list.add(new Pair<Vector, Value>(pair.get1(), value));
			map.put(pair.get1(), value);
		}
		ArrayGrid grid = new ArrayGrid(map);
		Data data2 = new GridData(grid);
		return data2;
	}

	/**
	 * Returns the derivative of the velocity vector with time. See <a
	 * href="http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations"
	 * >wikipedia article</a>.
	 * 
	 * @param data
	 * @param position
	 * @return
	 */
	private Vector getVelocityDerivativeWithTime(Data data, Vector position) {
		Value value = data.getValue(position);
		Vector velocityLaplacian = data.getVelocityLaplacian(position);
		Vector pressureGradient = data.getPressureGradient(position);
		Matrix velocityJacobian = data.getVelocityJacobian(position);
		// From Navier-Stokes conservation of momentum equation
		return velocityLaplacian.multiply(value.viscosity)
				.minus(pressureGradient).divide(value.density)
				.add(Constants.gravity)
				.minus(velocityJacobian.multiply(value.velocity));
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
		Vector dvdt = getVelocityDerivativeWithTime(data, position);
		Value value = data.getValue(position);
		return value.velocity.add(dvdt.multiply(timeDelta));
	}

	/**
	 * Returns velocity and pressure after timeDelta has passed. Obtains an
	 * initial estimate of velocity by solving the Navier-Stokes momentum
	 * equation. Then solves the continuity equation for a value of pressure
	 * which is then used to calculate the velocity and iterates similarly till
	 * the Continuity equation is satisfied to an acceptable degree of
	 * precision. See <a
	 * href="http://en.wikipedia.org/wiki/Pressure-correction_method">here</a>
	 * for details of the pressure correction method.
	 * 
	 * @param position
	 * @param timeDelta
	 * @param differentiator
	 * @param data
	 * @param stepHint
	 * @return
	 */
	Value getValueAfterTime(Data data, Vector position, double timeDelta) {

		log.fine("getting value after time=" + timeDelta);
		Value value0 = data.getValue(position);
		if (value0.isWall())
			return value0;

		log.fine("initial value:" + value0);

		Vector v1 = getVelocityAfterTime(data, position, timeDelta);
		log.fine("first guess velocity=" + v1);

		// if stopped now then continuity (conservation of mass) equation might
		// not be satisfied. Perform pressure correction:
		Function<Double, Double> f = createContinuityFunction(data, position,
				v1, timeDelta);

		double p0 = value0.pressure;
		// solve f = 0 for pressure p where f is defined:
		// TODO best value for precision?
		double precision = 0.001;
		// TODO best value for step size?
		double pressureStepSize = 1; // in Pa
		Double pressure = NewtonsMethodSolver.solve(f, p0, pressureStepSize,
				precision, MAX_NEWTONS_ITERATIONS);
		if (pressure == null)
			pressure = p0;

		// substitute the pressure back into the conservation of momentum eqn to
		// solve for velocity again
		Value v = data.getValue(position);
		Value valueNext = new Value(v.velocity, pressure, v.depth, v.density,
				v.viscosity);
		Data data2 = new DataOverride(data, position, valueNext);
		Vector v2 = getVelocityAfterTime(data2, position, timeDelta);

		Value result = new Value(v2, pressure, value0.depth, value0.density,
				value0.viscosity);
		log.fine("returning value=" + result);
		return result;
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
				Data data2 = new DataOverride(data, position, valueNext);
				return data2.getPressureCorrectiveFunction(position);
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
