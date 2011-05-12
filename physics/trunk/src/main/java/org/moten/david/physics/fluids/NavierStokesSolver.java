package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;

/**
 * Solver for Newtonian incompressible fluids, in particular seawater. Based on
 * <a
 * href="http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations">this</a>.
 * 
 * @author dxm
 * 
 */
public class NavierStokesSolver {

	private static Vector g = new Vector(0, 0, -9.8);

	/**
	 * Returns the value of the field of given type after time
	 * <code>timeDelta</code> using given stepHints for differentiation if
	 * required. Uses Finite Difference Method for solution of Navier-Stokes
	 * equations in cartesian coordinates.
	 * 
	 * @param timeDelta
	 * @param maxDelta
	 * @param data
	 * @param position
	 * @return
	 */
	private double getValueInTime(Vector position, Vector.Direction direction,
			double timeDelta, Differentiator differentiator, Data data,
			Vector stepHint) {

		// evaluate fields at position
		double u = data.getField(Direction.X).apply(position);
		double v = data.getField(Direction.Y).apply(position);
		double w = data.getField(Direction.Z).apply(position);
		Function<Vector, Double> pressureField = data.getPressure();
		double p = pressureField.apply(position);
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
				pressureField, direction, position, p, stepHint);
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
		double et = (-pDiff + mu * (exx + eyy + ezz)) / rho + g.get(direction)
				- (u * ex + v * ey + w * ez);
		return eValue + timeDelta * et;
	}

	private double differentiate(Differentiator differentiator,
			WallFinder wallFinder, Function<Vector, Double> f, Direction t,
			Vector position, double value, Vector stepHint) {
		return differentiator.differentiate(wallFinder, f, t, position, value,
				stepHint.get(t));
	}

	private double differentiate2(Differentiator differentiator,
			WallFinder wallFinder, Function<Vector, Double> f, Direction t,
			Vector position, double value, Vector stepHint) {
		return differentiator.differentiate2(wallFinder, f, t, position, value,
				stepHint.get(t));
	}

	public Value getValueInTime(Vector position, double timeDelta,
			Differentiator differentiator, Data data, Vector stepHint) {
		Value f = new Value();
		f.u = getValueInTime(position, Direction.X, timeDelta, differentiator,
				data, stepHint);
		f.v = getValueInTime(position, Direction.Y, timeDelta, differentiator,
				data, stepHint);
		f.w = getValueInTime(position, Direction.Z, timeDelta, differentiator,
				data, stepHint);
		// TODO extract p
		f.p = 0;
		return f;
	}
}
