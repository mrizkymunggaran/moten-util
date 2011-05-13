package org.moten.david.physics.fluids;

import org.moten.david.util.math.Vector;

public interface WallFinder {
	/**
	 * Starting at <code>position</code>, finds the closest wall up to
	 * <code>maxDistance</code>away in the direction given by
	 * <code>direction</code> and the returns the distance to that wall. If
	 * there is no wall returns null.
	 * 
	 * @param maxDistance
	 * @param direction
	 *            , only possible values are 1,2,3 corresponding to x,y,z.
	 * @param position
	 * @return
	 */
	Double findWall(Vector position, double maxDistance);
}
