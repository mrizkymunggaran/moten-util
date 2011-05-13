package org.moten.david.physics.fluids;

import org.moten.david.util.math.Function;
import org.moten.david.util.math.Vector;

public interface Differentiator {
	Double differentiate(WallFinder wallFinder, Function<Vector, Double> f,
			Vector.Direction direction, Vector position, double value,
			double stepHint);

	Double differentiate2(WallFinder wallFinder, Function<Vector, Double> f,
			Vector.Direction direction, Vector position, double value,
			double stepHint);
}
