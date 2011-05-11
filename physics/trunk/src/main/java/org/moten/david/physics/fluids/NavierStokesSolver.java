package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;

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
	 * @param field
	 * @param position
	 * @return
	 */
	public double getValueInTime(Vector position, Vector.Direction direction,
			double timeDelta, Differentiator differentiator, Field field,
			Vector stepHint) {

		// evaluate fields at position
		double u = field.u().apply(position);
		double v = field.v().apply(position);
		double w = field.w().apply(position);
		double p = field.p().apply(position);
		double rho = field.rho().apply(position);

		// element refers to a specific field selection
		Function<Vector, Double> element = field.getField(direction);
		double eValue = element.apply(position);

		// wrt = with respect to

		// differentiate p wrt the direction
		double pDiff = differentiate(differentiator, field.p(), direction,
				position, p, stepHint);
		// differentiate element wrt x
		double ex = differentiate(differentiator, element, Direction.X,
				position, eValue, stepHint);
		// differentiate element wrt y
		double ey = differentiate(differentiator, element, Direction.Y,
				position, eValue, stepHint);

		// differentiate element wrt z
		// exert the Continuous condition of Navier-Stokes
		double ez = -ex - ey;

		// 2nd derivative of element at position wrt x
		double exx = differentiate2(differentiator, element, Direction.X,
				position, u, stepHint);
		// 2nd derivative of element at position wrt y
		double eyy = differentiate2(differentiator, element, Direction.Y,
				position, v, stepHint);
		// 2nd derivative of element at position wrt z
		double ezz = differentiate2(differentiator, element, Direction.Z,
				position, w, stepHint);

		double et = (-pDiff + u * (exx + eyy + ezz) + rho * g.get(direction) - (u
				* ex + v * ey + w * ez))
				/ rho;
		return eValue + timeDelta * et;
	}

	private double differentiate(Differentiator differentiator,
			Function<Vector, Double> f, Direction t, Vector position,
			double value, Vector stepHint) {
		return differentiator.differentiate(f, t, position, value,
				stepHint.get(t));
	}

	private double differentiate2(Differentiator differentiator,
			Function<Vector, Double> f, Direction t, Vector position,
			double value, Vector stepHint) {
		return differentiator.differentiate2(f, t, position, value,
				stepHint.get(t));
	}
}
