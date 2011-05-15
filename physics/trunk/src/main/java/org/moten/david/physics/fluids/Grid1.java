package org.moten.david.physics.fluids;

import static org.moten.david.util.math.Direction.X;
import static org.moten.david.util.math.Direction.Y;
import static org.moten.david.util.math.Vector.vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.moten.david.util.math.Direction;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

public class Grid1 implements Grid<Value> {

	private final Value[] values;

	public Grid1(Value... values) {
		this.values = values;
		map.put(vector(0, 0, 0), values[0]);
		map.put(vector(-1, 0, 0), values[1]);
		map.put(vector(1, 0, 0), values[2]);
		map.put(vector(0, -1, 0), values[3]);
		map.put(vector(0, 1, 0), values[4]);
		map.put(vector(0, 0, -1), values[5]);
		map.put(vector(0, 0, 1), values[6]);
	}

	private final Map<Vector, Value> map = new HashMap<Vector, Value>();

	@Override
	public Value get(Vector position) {
		return map.get(position);
	}

	@Override
	public Vector getNeighbour(Vector position, Direction direction,
			boolean positive) {
		if (positive) {
			if (X.equals(direction)) {
				return vector(1, 0, 0);
			} else if (Y.equals(direction)) {
				return vector(0, 1, 0);
			} else {
				return vector(0, 0, 1);
			}
		} else {
			if (X.equals(direction)) {
				return vector(-1, 0, 0);
			} else if (Y.equals(direction)) {
				return vector(0, -1, 0);
			} else {
				return vector(0, 0, -1);
			}
		}
	}

	@Override
	public Iterable<Pair<Vector, Value>> entries() {
		return new Iterable<Pair<Vector, Value>>() {

			@Override
			public Iterator<Pair<Vector, Value>> iterator() {
				return new Iterator<Pair<Vector, Value>>() {
					Iterator<Entry<Vector, Value>> it = map.entrySet()
							.iterator();

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public Pair<Vector, Value> next() {
						Entry<Vector, Value> entry = it.next();
						return new Pair<Vector, Value>(entry.getKey(),
								entry.getValue());
					}

					@Override
					public void remove() {
						throw new RuntimeException("not implemented");
					}
				};
			}
		};
	}
}
