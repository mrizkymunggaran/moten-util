package org.moten.david.physics.fluids;

import java.util.List;

import org.moten.david.util.math.Function;
import org.moten.david.util.math.Vector;
import org.moten.david.util.math.Vector.Direction;

public class InterpolatedData extends AbstractData {

	private final List<Value> values;
	private final Value min;
	private final Value max;

	public InterpolatedData(List<Value> values) {
		this.values = values;
		this.min = findExtreme(true);
		this.max = findExtreme(false);
	}

	private Value findExtreme(boolean minimum) {
		Double u = null;
		Double v = null;
		Double w = null;
		Double p = null;
		int sign;
		if (minimum)
			sign = -1;
		else
			sign = 1;
		for (Value value : values) {
			if (u == null || Math.signum(value.velocity.x - u) == sign)
				u = value.velocity.x;
			if (v == null || Math.signum(value.velocity.y - v) == sign)
				v = value.velocity.y;
			if (w == null || Math.signum(value.velocity.z - w) == sign)
				w = value.velocity.z;
			if (p == null || Math.signum(value.pressure - p) == sign)
				p = value.pressure;
		}
		return new Value(new Vector(u, v, w), p);
	}

	@Override
	public Function<Vector, Double> getField(Direction direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Vector, Double> getPressure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WallFinder getWallFinder(Direction direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Vector, Double> getDensity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Vector, Double> getDynamicViscosity() {
		// TODO Auto-generated method stub
		return null;
	}

}
