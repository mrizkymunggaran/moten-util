package org.moten.david.physics.fluids;

import org.moten.david.util.math.Vector;

public class Value {
	public Value(Vector velocity, double pressure) {
		super();
		this.velocity = velocity;
		this.pressure = pressure;
	}

	public Vector velocity;
	public double pressure;
}