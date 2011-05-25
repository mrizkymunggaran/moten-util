package org.moten.david.physics.fluids;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.moten.david.util.math.Direction;
import org.moten.david.util.math.Neighbours;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Triple;
import org.moten.david.util.math.Vector;

public class ArrayGrid implements Grid<Value> {

	private final Map<Vector, Value> map;
	private final Map<Triple<Vector, Direction, Boolean>, Vector> n;

	/**
	 * Constructor. <code>map</code> should contain a grid of points all on a
	 * discrete 3d grid so that a cell's neighbour in say the y direction has
	 * the same x and z position. This is an important assumption in calculation
	 * of derivatives at each point. You can think of the points then as the
	 * centres of a mass of aligned boxes.
	 * 
	 * @param map
	 */
	public ArrayGrid(Map<Vector, Value> map) {
		// make a defensive copy because we are going to modify it
		this.map = new HashMap<Vector, Value>(map);
		this.n = Neighbours.findNeighbours(map.keySet());

		HashMap<Vector, TreeSet<Double>> lookups = Neighbours.getLookups(map
				.keySet());
		Set<Vector> boundary = new HashSet<Vector>();
		for (Vector vector : lookups.keySet()) {
			for (Direction d : Direction.values())
				if (Double.isInfinite(vector.get(d))) {
					TreeSet<Double> values = lookups.get(vector);
					boundary.add(vector.modify(d, values.first()));
					boundary.add(vector.modify(d, values.last()));
				}
		}
		for (Vector vector : boundary) {
			this.map.get(vector).copy().setBoundary(true);
		}
	}

	@Override
	public Value get(Vector position) {
		Value result = map.get(position);
		return result;
	}

	@Override
	public Vector getNeighbour(Vector position, Direction direction,
			boolean positive) {
		return n.get(new Triple<Vector, Direction, Boolean>(position,
				direction, positive));
	}

	@Override
	public Iterable<Pair<Vector, Value>> getEntries() {
		return new Iterable<Pair<Vector, Value>>() {

			@Override
			public Iterator<Pair<Vector, Value>> iterator() {
				return new Iterator<Pair<Vector, Value>>() {
					private final Iterator<Entry<Vector, Value>> entries = map
							.entrySet().iterator();

					@Override
					public boolean hasNext() {
						return entries.hasNext();
					}

					@Override
					public Pair<Vector, Value> next() {
						Entry<Vector, Value> entry = entries.next();
						return new Pair<Vector, Value>(entry.getKey(),
								entry.getValue());
					}

					@Override
					public void remove() {
						throw new RuntimeException("remove not implemented");
					}
				};
			}
		};
	}
}
