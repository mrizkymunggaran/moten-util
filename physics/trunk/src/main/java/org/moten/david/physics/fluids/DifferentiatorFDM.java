package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;

public class DifferentiatorFDM implements Differentiator {

	public synchronized Double differentiate(Function<Vector, Double> f,
			Direction direction, Vector position, double value, double stepHint) {
		// TODO use findWall
		Vector position2 = position.modify(direction, position.get(direction)
				+ stepHint);
		Double value2 = f.apply(position2);
		return (value2 - value) / stepHint;
	}

	public Double differentiate2(Function<Vector, Double> f,
			Direction direction, Vector position, double value, double stepHint) {
		// TODO use findWall
		Vector position2 = position.modify(direction, position.get(direction)
				+ stepHint);
		Vector position3 = position.modify(direction, position.get(direction)
				- stepHint);
		Double value2 = f.apply(position2);
		Double value3 = f.apply(position3);
		return (value2 + value3 - 2 * value) / stepHint / stepHint;
	}

}
