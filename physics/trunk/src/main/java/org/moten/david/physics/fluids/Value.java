package org.moten.david.physics.fluids;

import org.moten.david.util.math.Vector;

public class Value {

	@Override
	public String toString() {
		return "Value [velocity=" + velocity + ", pressure=" + pressure
				+ ", depth=" + depth + ", density=" + density + ", viscosity="
				+ viscosity + "]";
	}

	public Value(Vector velocity, double pressure, double depth,
			double density, double viscosity) {
		super();
		this.velocity = velocity;
		this.pressure = pressure;
		this.depth = depth;
		this.density = density;
		this.viscosity = viscosity;
	}

	public Vector velocity;
	public double pressure;
	public double depth;
	public final double density;
	public final double viscosity;

	public boolean isWall() {
		return pressure == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(density);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(depth);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(pressure);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((velocity == null) ? 0 : velocity.hashCode());
		temp = Double.doubleToLongBits(viscosity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Value other = (Value) obj;
		if (Double.doubleToLongBits(density) != Double
				.doubleToLongBits(other.density))
			return false;
		if (Double.doubleToLongBits(depth) != Double
				.doubleToLongBits(other.depth))
			return false;
		if (Double.doubleToLongBits(pressure) != Double
				.doubleToLongBits(other.pressure))
			return false;
		if (velocity == null) {
			if (other.velocity != null)
				return false;
		} else if (!velocity.equals(other.velocity))
			return false;
		if (Double.doubleToLongBits(viscosity) != Double
				.doubleToLongBits(other.viscosity))
			return false;
		return true;
	}

}
