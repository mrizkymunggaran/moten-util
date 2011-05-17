package org.moten.david.util.math;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility methods for finding neighbours of 3D points.
 * 
 */
public class Neighbours {

	/**
	 * Finds neighbours on the assumption that all vectors are part of a
	 * discretized grid (3 dimensional).
	 * 
	 * @param map
	 * @return
	 */
	public static Map<Triple<Vector, Direction, Boolean>, Vector> findNeighbours(
			Set<Vector> vectors) {

		HashMap<Vector, TreeSet<Double>> lookups = getLookups(vectors);

		Map<Triple<Vector, Direction, Boolean>, Vector> n = new HashMap<Triple<Vector, Direction, Boolean>, Vector>();
		for (Vector vector : vectors) {
			for (Direction direction : Direction.values()) {
				for (Boolean isPositive : new Boolean[] { true, false }) {
					Vector closest = getClosest(
							lookups.get(vectorRepresentativeIgnoring(vector,
									direction)), vector, direction, isPositive);
					if (closest != null)
						n.put(new Triple<Vector, Direction, Boolean>(vector,
								direction, isPositive), closest);
				}
			}
		}
		return n;
	}

	public static Vector vectorRepresentativeIgnoring(Vector vector,
			Direction direction) {
		return vector.modify(direction, Double.POSITIVE_INFINITY);
	}

	/**
	 * Returns lookups based on one of the 3 dimensions set to 0.
	 * 
	 * @param vectors
	 * @return
	 */
	public static HashMap<Vector, TreeSet<Double>> getLookups(
			Set<Vector> vectors) {
		HashMap<Vector, TreeSet<Double>> lookups = new HashMap<Vector, TreeSet<Double>>();
		for (Vector p : vectors) {
			for (Direction direction : Direction.values()) {
				Vector v = vectorRepresentativeIgnoring(p, direction);
				if (lookups.get(v) == null)
					lookups.put(v, new TreeSet<Double>());
				lookups.get(v).add(p.get(direction));
			}
		}
		return lookups;
	}

	/**
	 * Returns the closest value in <code>set</code> for the ordinate given (
	 * <code>direction</code>) in the positive or negative direction for that
	 * ordinate (<code>positive</code>).
	 * 
	 * @param set
	 * @param vector
	 * @param direction
	 * @param positive
	 * @return
	 */
	private static Vector getClosest(TreeSet<Double> set, Vector vector,
			Direction direction, boolean positive) {
		Double d;
		if (positive)
			d = set.higher(vector.get(direction));
		else
			d = set.lower(vector.get(direction));
		if (d == null)
			return null;
		else
			return vector.modify(direction, d);
	}

}
