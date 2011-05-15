package org.moten.david.physics.fluids;

import org.moten.david.util.math.Direction;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

public interface Grid<T> {

	T get(Vector position);

	Vector getNeighbour(Vector position, Direction direction, boolean positive);

	Iterable<Pair<Vector, Value>> entries();
}
