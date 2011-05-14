package org.moten.david.physics.fluids;

import org.moten.david.util.math.Matrix;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

/**
 * Returns data access functions about a specific region.
 * 
 * @author dxm
 * 
 */
public interface Data {

	Value getValue(Vector position);

	Vector getPressureGradient(Vector position);

	Matrix getVelocityJacobian(Vector position);

	Vector getVelocityLaplacian(Vector position);

	Data copy();

	Iterable<Pair<Vector, Value>> getValues();

}
