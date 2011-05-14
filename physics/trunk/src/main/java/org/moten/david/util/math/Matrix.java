package org.moten.david.util.math;

public class Matrix {

	private final Vector row1;
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

}
