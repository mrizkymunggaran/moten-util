package org.moten.david.physics.fluids;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.moten.david.util.math.Direction;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Triple;
import org.moten.david.util.math.Vector;

public class ArrayGrid implements Grid<Value> {

	private final Map<Vector, Value> map;
	private final Map<Triple<Vector, Direction, Boolean>, Vector> n = new HashMap<Triple<Vector, Direction, Boolean>, Vector>();

	public ArrayGrid(Map<Vector, Value> map) {
		this.map = map;
		prepareNeighbours();
	}

	private void prepareNeighbours() {
		for (Entry<Vector, Value> entry : map.entrySet()) {
			// brute force
		}
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
		// TODO Auto-generated method stub
		return null;
	}

}
