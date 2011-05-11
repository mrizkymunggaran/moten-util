package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;

public class NavierStokesSolver {

	private static Vector g = new Vector(0, 0, -9.8);

	/**
	 * Returns the value of the field of given type after time
	 * <code>timeDelta</code> using given stepHints for differentiation if
	 * required.
	 * 
	 * @param timeDelta
	 * @param maxDelta
	 * @param field
	 * @param position
	 * @return
	 */
	public double getValueInTime(double timeDelta, Vector.Direction direction,
			Differentiator differentiator, Field field, Vector position,
			Vector stepHint) {

		Function<Vector, Double> element = field.getField(direction);
		double eValue = element.apply(position);

		double u = field.u().apply(position);
		double v = field.v().apply(position);
		double w = field.w().apply(position);
		double p = field.p().apply(position);
		double rho = field.rho().apply(position);

		double pDiff = differentiate(differentiator, field.p(), direction,
				position, p, stepHint);
		double ex = differentiate(differentiator, element, Direction.X,
				position, eValue, stepHint);
		double ey = differentiate(differentiator, element, Direction.Y,
				position, eValue, stepHint);

		// now exert the Continuous condition of Navier-Stokes
		double ez = -ex - ey;

		double exx = differentiate2(differentiator, element, Direction.X,
				position, u, stepHint);
		double eyy = differentiate2(differentiator, element, Direction.Y,
				position, v, stepHint);
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
