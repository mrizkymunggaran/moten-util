package org.moten.david.util.math;

public enum Direction {
	X(1), Y(2), Z(3);

	private int number;

	private Direction(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}
}