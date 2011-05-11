package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;

public interface Data {

	/**
	 * Returns density
	 * 
	 * @return
	 */
	Function<Vector, Double> rho();

	/**
	 * Returns u, v, w for Direction X,Y,Z.
	 * 
	 * @param type
	 * @return
	 */
	Function<Vector, Double> getField(Direction direction);

	Function<Vector, Double> getPressure();

	WallFinder getWallFinder(Direction direction);

	Function<Vector, Double> dynamicViscosity();

}
