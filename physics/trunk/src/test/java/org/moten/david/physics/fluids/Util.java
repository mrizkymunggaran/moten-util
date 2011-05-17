package org.moten.david.physics.fluids;

import org.moten.david.util.math.Vector;

public class Util {

	public static Value val(double xSpeed, double ySpeed, double zSpeed,
			double pressure) {
		Value value = new Value(new Vector(xSpeed, ySpeed, zSpeed), pressure,
				-10, 1000.0, 0.00108);
		return value;
	}

	public static Value val(double pressure) {
		return val(0, 0, 0, pressure);
	}

	public static Value val(double xSpeed, double ySpeed, double zSpeed) {
		return val(xSpeed, ySpeed, zSpeed, 1000);
	}

}
