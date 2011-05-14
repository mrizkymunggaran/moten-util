package org.moten.david.physics.fluids;

import static org.moten.david.util.math.Vector.vector;

import org.moten.david.util.math.Direction;
import org.moten.david.util.math.Matrix;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

public class GridData implements Data {

	@Override
	public Value getValue(Vector position) {
		// TODO Auto-generated method stub
		return null;
	}

	private Vector getNeighbour(Vector position, Direction direction,
			boolean positive) {
		// TODO
		return null;
	}

	@Override
	public Vector getPressureGradient(Vector position) {
		double pressure = getValue(position).pressure;
		Vector x1 = getNeighbour(position, Direction.X, false);
		Vector x2 = getNeighbour(position, Direction.X, true);
		Vector y1 = getNeighbour(position, Direction.Y, false);
		Vector y2 = getNeighbour(position, Direction.Y, true);
		Vector z1 = getNeighbour(position, Direction.Z, false);
		Vector z2 = getNeighbour(position, Direction.Z, true);
		double pX1 = getPressure(x1, pressure);
		double pX2 = getPressure(x2, pressure);
		double pY1 = getPressure(y1, pressure);
		double pY2 = getPressure(y2, pressure);
		double pZ1 = getPressure(z1, pressure);
		double pZ2 = getPressure(z2, pressure);

		double gradX = (pX2 - pX1) / (x2.x - x1.x);
		double gradY = (pY2 - pY1) / (y2.x - y1.x);
		double gradZ = (pZ2 - pZ1) / (z2.x - z1.x);
		return vector(gradX, gradY, gradZ);
	}

	/**
	 * Returns <code>defaultPressure</code> if there is a wall at
	 * <code>position</code>. Otherwise returns the pressure value at
	 * <code>position</code>.
	 * 
	 * @param position
	 * @param defaultPressure
	 * @return
	 */
	private double getPressure(Vector position, double defaultPressure) {
		Value value = getValue(position);
		if (value.isWall())
			return defaultPressure;
		else
			return value.pressure;
	}

	@Override
	public Matrix getVelocityJacobian(Vector position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector getVelocityLaplacian(Vector position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Pair<Vector, Value>> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
