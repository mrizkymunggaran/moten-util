package org.moten.david.physics.fluids;

public class Vector {

	public static enum Direction {
		X(1), Y(2), Z(3);

		private int number;

		private Direction(int number) {
			this.number = number;
		}

		public int getNumber() {
			return number;
		}
	}

	double x;
	double y;
	double z;

	public Vector(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
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
}
