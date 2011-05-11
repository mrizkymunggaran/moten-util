package org.moten.david.physics.fluids;

public interface Differentiator {
	Double differentiate(Function<Vector, Double> f, Vector.Direction direction,
			Vector position, double value, double stepHint);

	Double differentiate2(Function<Vector, Double> f, Vector.Direction direction,
			Vector position, double value, double stepHint);
}
