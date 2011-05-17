package org.moten.david.physics.fluids;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
		this.map = map;
		this.n = Neighbours.findNeighbours(map.keySet());
	}

	@Override
	public Value get(Vector position) {
		Value result = map.get(position);
		if (result == null)
			throw new RuntimeException("position not found!");
		return result;
	}

	@Override
	public Vector getNeighbour(Vector position, Direction direction,
			boolean positive) {
		return n.get(new Triple<Vector, Direction, Boolean>(position,
				direction, positive));
	}

	@Override
	public Iterable<Pair<Vector, Value>> entries() {
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
