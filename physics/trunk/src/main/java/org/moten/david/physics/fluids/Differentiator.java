package org.moten.david.physics.fluids;

public interface Differentiator {
	Double differentiate(WallFinder wallFinder, Function<Vector, Double> f,
			Vector.Direction direction, Vector position, double value,
			double stepHint);

	Double differentiate2(WallFinder wallFinder, Function<Vector, Double> f,
			Vector.Direction direction, Vector position, double value,
			double stepHint);
}
