package org.moten.david.util.math;

public class Matrix {

	private final Vector row1;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((row1 == null) ? 0 : row1.hashCode());
		result = prime * result + ((row2 == null) ? 0 : row2.hashCode());
		result = prime * result + ((row3 == null) ? 0 : row3.hashCode());
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
		Matrix other = (Matrix) obj;
		if (row1 == null) {
			if (other.row1 != null)
				return false;
		} else if (!row1.equals(other.row1))
			return false;
		if (row2 == null) {
			if (other.row2 != null)
				return false;
		} else if (!row2.equals(other.row2))
			return false;
		if (row3 == null) {
			if (other.row3 != null)
				return false;
		} else if (!row3.equals(other.row3))
			return false;
		return true;
	}

	private final Vector row2;
	private final Vector row3;

	public Matrix(Vector row1, Vector row2, Vector row3) {
		this.row1 = row1;
		this.row2 = row2;
		this.row3 = row3;
	}

	public Vector multiply(Vector v) {
		return new Vector(row1.dot(v), row2.dot(v), row3.dot(v));
	}

	public double diagonalSum() {
		return row1.x + row2.y + row3.z;
	}

	public Vector sumColumnVectors() {
		return row1.add(row2).add(row3);
	}

	@Override
	public String toString() {
		return "[" + row1 + ",\n" + row2 + "\n," + row3 + "]";
	}

}
