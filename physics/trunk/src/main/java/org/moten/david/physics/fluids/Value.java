package org.moten.david.physics.fluids;

import org.moten.david.util.math.Vector;

public class Value {

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

}
