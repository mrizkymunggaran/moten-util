package org.moten.david.physics.fluids;

import static org.moten.david.util.math.Vector.vector;

import org.moten.david.util.math.Vector;

public class Constants {

	public static final double FORCE_OF_GRAVITY = 9.8;// metres per second
														// squared
	public static Vector gravity = vector(0, 0, -Constants.FORCE_OF_GRAVITY);
}
