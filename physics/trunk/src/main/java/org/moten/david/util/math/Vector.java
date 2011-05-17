package org.moten.david.util.math;

import java.util.List;

public class Vector {

	@Override
	public String toString() {
		return "Vector [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	public double x;
	public double y;
	public double z;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
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
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	public Vector(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(List<Double> list) {
		this(list.get(0), list.get(1), list.get(2));
	}

	public double get(Direction t) {
		return get(t.getNumber());
	}

	public double get(int i) {
		if (i == 1)
			return x;
		else if (i == 2)
			return y;
		else if (i == 3)
			return z;
		else
			throw new IndexOutOfBoundsException("" + i);
	}

	public Vector modify(Direction direction, double value) {
		if (Direction.X.equals(direction))
			return new Vector(value, y, z);
		else if (Direction.Y.equals(direction))
			return new Vector(x, value, z);
		else if (Direction.Z.equals(direction))
			return new Vector(x, y, value);
		else
			throw new RuntimeException("unhandled Direction: " + direction);
	}

	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y, z + v.z);
	}

	public Vector minus(Vector v) {
		return new Vector(x - v.x, y - v.y, z - v.z);
	}

	public Vector multiply(double scalar) {
		return new Vector(x * scalar, y * scalar, z * scalar);
	}

	public Vector divide(double scalar) {
		if (scalar == 0)
			throw new RuntimeException("division by zero");
		return new Vector(x / scalar, y / scalar, z / scalar);
	}

	public double dot(Vector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public static Vector ORIGIN = new Vector(0, 0, 0);

	public static Vector vector(double x, double y, double z) {
		if (Double.isNaN(x))
			throw new RuntimeException(
					"NaN cannot be a parameter to a Vector - x");
		if (Double.isNaN(y))
			throw new RuntimeException(
					"NaN cannot be a parameter to a Vector - y");
		if (Double.isNaN(z))
			throw new RuntimeException(
					"NaN cannot be a parameter to a Vector - z");
		return new Vector(x, y, z);
	}
}
