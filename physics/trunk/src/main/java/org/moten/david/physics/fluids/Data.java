package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;
import org.moten.david.util.math.Function;

/**
 * Returns data access functions about a specific region.
 * 
 * @author dxm
 * 
 */
public interface Data {

	/**
	 * Returns density function in SI units.
	 * 
	 * @return
	 */
	Function<Vector, Double> getDensity();

	/**
	 * Returns u, v, w functions for Direction X,Y,Z.
	 * 
	 * @param type
	 * @return
	 */
	Function<Vector, Double> getField(Direction direction);

	/**
	 * Return the velocity field.
	 * 
	 * @return
	 */
	Function<Vector, Vector> getVelocityField();

	/**
	 * Returns pressure function in SI units.
	 * 
	 * @return
	 */
	Function<Vector, Double> getPressure();

	/**
	 * Returns {@link WallFinder} for the given direction. Pressure should use
	 * the wallFinder for {@link Direction}.Z.
	 * 
	 * @param direction
	 * @return
	 */
	WallFinder getWallFinder(Direction direction);

	/**
	 * Returns the dynamic viscosity in SI units.
	 * 
	 * @return
	 */
	Function<Vector, Double> getDynamicViscosity();

}
