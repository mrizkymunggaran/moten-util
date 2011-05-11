package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;

public class DifferentiatorFDM implements Differentiator {

	public synchronized Double differentiate(WallFinder wallFinder,
			Function<Vector, Double> f, Direction direction, Vector position,
			double value, double stepHint) {
		double h = getStep(wallFinder, position, stepHint);
		Vector position2 = position.modify(direction, position.get(direction)
				+ h);
		Double value2 = f.apply(position2);
		return (value2 - value) / h;
	}

	private double getStep(WallFinder wallFinder, Vector position,
			double stepHint) {
		double h;
		Double wallDistance = wallFinder.findWall(position, stepHint);
		if (wallDistance == null || wallDistance == 0)
			h = stepHint;
		else
			h = wallDistance / 2;
		return h;
	}

	public Double differentiate2(WallFinder wallFinder,
			Function<Vector, Double> f, Direction direction, Vector position,
			double value, double stepHint) {
		double h = getStep(wallFinder, position, stepHint);
		Vector position2 = position.modify(direction, position.get(direction)
				+ h);
		Vector position3 = position.modify(direction, position.get(direction)
				- h);
		Double value2 = f.apply(position2);
		Double value3 = f.apply(position3);
		return (value2 + value3 - 2 * value) / h / h;
	}

}
