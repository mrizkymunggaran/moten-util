package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;

public interface Field {

	public static enum Pressure implements Item {
		PRESSURE;
	}

	public static Item[] items = new Item[] { Direction.X, Direction.Y,
			Direction.Z, Pressure.PRESSURE };

	/**
	 * Returns current in x direction.
	 * 
	 * @return
	 */
	Function<Vector, Double> u();

	/**
	 * Returns current in y direction;
	 * 
	 * @return
	 */
	Function<Vector, Double> v();

	/**
	 * Returns current in z direction.
	 * 
	 * @return
	 */
	Function<Vector, Double> w();

	/**
	 * Returns pressure
	 * 
	 * @return
	 */
	Function<Vector, Double> p();

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
	Function<Vector, Double> getField(Item item);

	/**
	 * Starting at <code>position</code>, finds the closest wall up to
	 * <code>maxDistance</code>away in the direction given by
	 * <code>direction</code>.
	 * 
	 * @param maxDistance
	 * @param direction
	 *            , only possible values are 1,2,3 corresponding to x,y,z.
	 * @param position
	 * @return
	 */
	Double findWall(Vector position, double maxDistance, int direction);
}
