package org.moten.david.physics.fluids;

import java.util.List;

import org.moten.david.util.math.Vector;

public abstract class InterpolatedData extends AbstractData {

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
		Double depth = null;
		Double density = null;
		Double viscosity = null;
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
			if (depth == null || Math.signum(value.depth - depth) == sign)
				depth = value.depth;
			if (density == null || Math.signum(value.density - density) == sign)
				density = value.density;
			if (viscosity == null
					|| Math.signum(value.viscosity - viscosity) == sign)
				viscosity = value.viscosity;
		}
		return new Value(new Vector(u, v, w), p, depth, density, viscosity);
	}

}
