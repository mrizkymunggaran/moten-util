package org.moten.david.util.math;

public class Rectangle {
	private Vector vector1;
	private Vector vector2;

	public Rectangle(Vector vector1, Vector vector2) {
		super();
		this.vector1 = vector1;
		this.vector2 = vector2;
	}

	public Vector getVector1() {
		return vector1;
	}

	public void setVector1(Vector vector1) {
		this.vector1 = vector1;
	}

	public Vector getVector2() {
		return vector2;
	}

	public void setVector2(Vector vector2) {
		this.vector2 = vector2;
	}

}
