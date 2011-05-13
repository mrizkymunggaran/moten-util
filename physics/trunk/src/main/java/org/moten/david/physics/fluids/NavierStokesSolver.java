package org.moten.david.physics.fluids;

import java.util.ArrayList;
import java.util.List;

import org.moten.david.physics.fluids.Vector.Direction;
import org.moten.david.util.math.Function;
import org.moten.david.util.math.NewtonsMethodSolver;

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
	private static Vector g = new Vector(0, 0, -9.8);

	/**
	 * Returns the value of the field of given type after time
	 * <code>timeDelta</code> using given stepHints for differentiation if
	 * required. Uses Finite Difference Method for solution of Navier-Stokes
	 * equations in cartesian coordinates (3D).
	 * 
	 * @param position
	 * @param velocity
	 * @param pressure
	 * @param direction
	 * @param timeDelta
	 * @param differentiator
	 * @param data
	 * @param stepHint
	 * @return
	 */
	private double getValueAfterTime(Vector position, Vector velocity,
			double pressure, Vector.Direction direction, double timeDelta,
			Differentiator differentiator, Data data, Vector stepHint) {

		double u = velocity.x;
		double v = velocity.y;
		double w = velocity.z;
		double p = pressure;
		double rho = data.getDensity().apply(position);
		double mu = data.getDynamicViscosity().apply(position);

		// element refers to a specific field selection
		Function<Vector, Double> element = data.getField(direction);
		double eValue = element.apply(position);
		WallFinder wallFinder = data.getWallFinder(direction);
		WallFinder pressureWallFinder = data.getWallFinder(Direction.Z);

		// wrt = with respect to

		// differentiate p wrt the direction
		double pDiff = differentiate(differentiator, pressureWallFinder,
				data.getPressure(), direction, position, p, stepHint);
		// differentiate element wrt x
		double ex = differentiate(differentiator, wallFinder, element,
				Direction.X, position, eValue, stepHint);
		// differentiate element wrt y
		double ey = differentiate(differentiator, wallFinder, element,
				Direction.Y, position, eValue, stepHint);
		// differentiate element wrt z
		double ez = differentiate(differentiator, wallFinder, element,
				Direction.Z, position, eValue, stepHint);

		// 2nd derivative of element at position wrt x
		double exx = differentiate2(differentiator, wallFinder, element,
				Direction.X, position, u, stepHint);
		// 2nd derivative of element at position wrt y
		double eyy = differentiate2(differentiator, wallFinder, element,
				Direction.Y, position, v, stepHint);
		// 2nd derivative of element at position wrt z
		double ezz = differentiate2(differentiator, wallFinder, element,
				Direction.Z, position, w, stepHint);

		// calculate derivative of element wrt t using Navier-Stokes
		// see http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations
		double et = (-pDiff + mu * (exx + eyy + ezz)) / rho + g.get(direction)
				- (u * ex + v * ey + w * ez);
		return eValue + timeDelta * et;
	}

	/**
	 * Returns the first derivative of f using a {@link WallFinder} to probe for
	 * domain boundaries.
	 * 
	 * @param differentiator
	 * @param wallFinder
	 * @param f
	 * @param t
	 * @param position
	 * @param value
	 * @param stepHint
	 * @return
	 */
	private static double differentiate(Differentiator differentiator,
			WallFinder wallFinder, Function<Vector, Double> f, Direction t,
			Vector position, double value, Vector stepHint) {
		return differentiator.differentiate(wallFinder, f, t, position, value,
				stepHint.get(t));
	}

	/**
	 * Returns the second derivative of f using a {@link WallFinder} to probe
	 * for domain boundaries.
	 * 
	 * @param differentiator
	 * @param wallFinder
	 * @param f
	 * @param t
	 * @param position
	 * @param value
	 * @param stepHint
	 * @return
	 */
	private static double differentiate2(Differentiator differentiator,
			WallFinder wallFinder, Function<Vector, Double> f, Direction t,
			Vector position, double value, Vector stepHint) {
		return differentiator.differentiate2(wallFinder, f, t, position, value,
				stepHint.get(t));
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
	public Value getValueAfterTime(Vector position, double timeDelta,
			Differentiator differentiator, Data data, Vector stepHint) {
		Vector v0 = data.getVelocityField().apply(position);
		double p0 = data.getPressure().apply(position);

		Vector v1 = getVelocityAfterTime(position, v0, p0, timeDelta,
				differentiator, data, stepHint);
		// if stopped now then continuity (conservation of mass) equation might
		// not be satisfied.
		//
		// Perform pressure correction as per
		// http://en.wikipedia.org/wiki/Pressure-correction_method
		//
		// i.e. solve f = 0 for pressure p where f is defined:
		//
		// Take the value of velocity v1 just created and find pressure s.t
		// du/dx + dv/dy + dw/dz = 0
		Function<Double, Double> f = createContinuityFunction(position, v1,
				timeDelta, differentiator, data, stepHint);

		// TODO best value for precision?
		double precision = 0.001;
		// TODO best value for step size?
		double pressureStepSize = 1; // in Pa
		Double pressure = NewtonsMethodSolver.solve(f, p0, pressureStepSize,
				precision, MAX_NEWTONS_ITERATIONS);
		if (pressure == null)
			pressure = p0;

		return new Value(v1, pressure);
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
	private Function<Double, Double> createContinuityFunction(
			final Vector position, final Vector velocity,
			final double timeDelta, final Differentiator differentiator,
			final Data data, final Vector stepHint) {
		return new Function<Double, Double>() {
			@Override
			public Double apply(Double p) {
				Vector velocityNext = getVelocityAfterTime(position, velocity,
						p, timeDelta, differentiator, data, stepHint);
				return divergence(position, velocityNext, differentiator, data,
						stepHint);
			}
		};
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
	private Vector getVelocityAfterTime(Vector position, Vector velocity,
			double pressure, double timeDelta, Differentiator differentiator,
			Data data, Vector stepHint) {
		double u = getValueAfterTime(position, velocity, pressure, Direction.X,
				timeDelta, differentiator, data, stepHint);
		double v = getValueAfterTime(position, velocity, pressure, Direction.Y,
				timeDelta, differentiator, data, stepHint);
		double w = getValueAfterTime(position, velocity, pressure, Direction.Z,
				timeDelta, differentiator, data, stepHint);
		return new Vector(u, v, w);
	}

	/**
	 * Returns the value of the Gradient operator at the given position. See <a
	 * href="http://en.wikipedia.org/wiki/Del">here</a>.
	 * 
	 * @param position
	 * @param velocity
	 * @param differentiator
	 * @param data
	 * @param stepHint
	 * @return
	 */
	private static Vector gradient(Vector position, Vector velocity,
			Differentiator differentiator, Data data, Vector stepHint) {
		// differentiate element wrt all directions x,y,z
		List<Double> list = new ArrayList<Double>();
		for (Direction direction : Direction.values()) {
			double d = differentiate(differentiator,
					data.getWallFinder(direction), data.getField(direction),
					direction, position, velocity.get(direction), stepHint);
			list.add(d);
		}
		// return a vector of the derivatives
		return new Vector(list);
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
	private static double divergence(Vector position, Vector velocity,
			Differentiator differentiator, Data data, Vector stepHint) {
		Vector gradient = gradient(position, velocity, differentiator, data,
				stepHint);
		return gradient.x + gradient.y + gradient.z;
	}
}
