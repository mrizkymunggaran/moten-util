package org.moten.david.physics.fluids;

import org.moten.david.util.math.Function;
import org.moten.david.util.math.Vector;
import org.moten.david.util.math.Vector.Direction;

public abstract class AbstractData implements Data {

	private final Function<Vector, Vector> velocityField = new Function<Vector, Vector>() {
		Function<Vector, Double> u = getField(Direction.X);
		Function<Vector, Double> v = getField(Direction.Y);
		Function<Vector, Double> w = getField(Direction.Z);

		@Override
		public Vector apply(Vector point) {
			return new Vector(u.apply(point), v.apply(point), w.apply(point));
		}
	};

	@Override
	public abstract Function<Vector, Double> getDensity();

	@Override
	public abstract Function<Vector, Double> getField(Direction direction);

	@Override
	public final Function<Vector, Vector> getVelocityField() {
		return velocityField;
	}

	@Override
	public abstract Function<Vector, Double> getPressure();

	@Override
	public abstract WallFinder getWallFinder(Direction direction);

	@Override
	public abstract Function<Vector, Double> getDynamicViscosity();

}
