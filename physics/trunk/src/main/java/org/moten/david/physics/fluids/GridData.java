package org.moten.david.physics.fluids;

import static org.moten.david.util.math.Vector.vector;

import java.util.logging.Logger;

import org.moten.david.util.math.Direction;
import org.moten.david.util.math.Matrix;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

public class GridData implements Data {

	private static Logger log = Logger.getLogger(GridData.class.getName());

	public GridData(Grid<Value> grid) {
		this.grid = grid;
	}

	private final Grid<Value> grid;

	@Override
	public Value getValue(Vector position) {
		return grid.get(position);
	}

	@Override
	public Iterable<Pair<Vector, Value>> getEntries() {
		return grid.getEntries();
	}

	private Vector getNeighbour(Vector position, Direction direction,
			boolean positive) {
		return grid.getNeighbour(position, direction, positive);
	}

	private static class Neighbours {
		Vector x1, x2, y1, y2, z1, z2;
		public Value valueX1, valueX2, valueY1, valueY2, valueZ1, valueZ2;

		@Override
		public String toString() {
			return "Neighbours [x1=" + x1 + ", x2=" + x2 + ", y1=" + y1
					+ ", y2=" + y2 + ", z1=" + z1 + ", z2=" + z2 + ", valueX1="
					+ valueX1 + ", valueX2=" + valueX2 + ", valueY1=" + valueY1
					+ ", valueY2=" + valueY2 + ", valueZ1=" + valueZ1
					+ ", valueZ2=" + valueZ2 + "]";
		}
	}

	private Neighbours getNeighbours(Vector position, Value value) {
		Value wallValue = new Value(new Vector(0, 0, 0), value.pressure,
				value.depth, value.density, value.viscosity);

		Neighbours n = new Neighbours();
		n.x1 = getNeighbour(position, Direction.X, false);
		n.x2 = getNeighbour(position, Direction.X, true);
		n.y1 = getNeighbour(position, Direction.Y, false);
		n.y2 = getNeighbour(position, Direction.Y, true);
		n.z1 = getNeighbour(position, Direction.Z, false);
		n.z2 = getNeighbour(position, Direction.Z, true);
		Value wallValueZ1 = new Value(Vector.ORIGIN, value.pressure
				+ value.density * Constants.FORCE_OF_GRAVITY
				* (position.z - n.z1.z), value.depth + (position.z - n.z1.z),
				value.density, value.viscosity);
		Value wallValueZ2 = new Value(Vector.ORIGIN, value.pressure
				- value.density * Constants.FORCE_OF_GRAVITY
				* (n.z2.z - position.z), value.depth + (n.z2.z - position.z),
				value.density, value.viscosity);
		n.valueX1 = getValue(n.x1, wallValue);
		n.valueX2 = getValue(n.x2, wallValue);
		n.valueY1 = getValue(n.y1, wallValue);
		n.valueY2 = getValue(n.y2, wallValue);
		n.valueZ1 = getValue(n.z1, wallValueZ1);
		n.valueZ2 = getValue(n.z2, wallValueZ2);
		return n;
	}

	@Override
	public Vector getPressureGradient(Vector position) {
		Value value = getValue(position);
		Neighbours n = getNeighbours(position, value);
		double gradX = (n.valueX2.pressure - n.valueX1.pressure)
				/ (n.x2.x - n.x1.x);
		double gradY = (n.valueY2.pressure - n.valueY1.pressure)
				/ (n.y2.y - n.y1.y);
		double gradZ = (n.valueZ2.pressure - n.valueZ1.pressure)
				/ (n.z2.z - n.z1.z);
		Vector result = vector(gradX, gradY, gradZ);
		return result;
	}

	/**
	 * Returns <code>nullValue</code> if <code>position</code> is null.
	 * Otherwise returns {@link Value} at <code>position</code>.
	 * 
	 * @param position
	 * @param defaultPressure
	 * @return
	 */
	private Value getValue(Vector position, Value wallValue) {
		Value value = getValue(position);
		if (value.isWall())
			return wallValue;
		else
			return value;
	}

	private Vector getVelocityDerivative(Vector position, Direction direction) {
		Value value = getValue(position);

		Neighbours n = getNeighbours(position, value);
		double gradX = (n.valueX2.velocity.get(direction) - n.valueX1.velocity
				.get(direction)) / (n.x2.x - n.x1.x);
		double gradY = (n.valueY2.velocity.get(direction) - n.valueY1.velocity
				.get(direction)) / (n.y2.y - n.y1.y);
		double gradZ = (n.valueZ2.velocity.get(direction) - n.valueZ1.velocity
				.get(direction)) / (n.z2.z - n.z1.z);
		return vector(gradX, gradY, gradZ);
	}

	@Override
	public Matrix getVelocityJacobian(Vector position) {
		Vector dvdx = getVelocityDerivative(position, Direction.X);
		Vector dvdy = getVelocityDerivative(position, Direction.Y);
		Vector dvdz = getVelocityDerivative(position, Direction.Z);
		Matrix m = new Matrix(dvdx, dvdy, dvdz);
		return m;
	}

	Vector getVelocity2ndDerivative(Vector position, Direction direction) {
		Value value = getValue(position);
		Value wallValue = new Value(new Vector(0, 0, 0), value.pressure,
				value.depth, value.density, value.viscosity);
		Neighbours n = getNeighbours(position, wallValue);
		double d2X = (n.valueX2.velocity.get(direction)
				+ n.valueX1.velocity.get(direction) - 2 * value.velocity
				.get(direction)) / sqr(n.x2.x - n.x1.x);
		double d2Y = (n.valueY2.velocity.get(direction)
				+ n.valueY1.velocity.get(direction) - 2 * value.velocity
				.get(direction)) / sqr(n.y2.y - n.y1.y);
		double d2Z = (n.valueZ2.velocity.get(direction)
				+ n.valueZ1.velocity.get(direction) - 2 * value.velocity
				.get(direction)) / sqr(n.z2.z - n.z1.z);
		return vector(d2X, d2Y, d2Z);
	}

	private Vector getPressure2ndDerivative(Vector position) {
		Value value = getValue(position);
		Value wallValue = new Value(new Vector(0, 0, 0), value.pressure,
				value.depth, value.density, value.viscosity);
		Neighbours n = getNeighbours(position, wallValue);
		double d2X = (n.valueX2.pressure + n.valueX1.pressure - 2 * value.pressure)
				/ sqr(n.x2.x - n.x1.x);
		double d2Y = (n.valueY2.pressure + n.valueY1.pressure - 2 * value.pressure)
				/ sqr(n.y2.y - n.y1.y);
		double d2Z = (n.valueZ2.pressure + n.valueZ1.pressure - 2 * value.pressure)
				/ sqr(n.z2.z - n.z1.z);
		return vector(d2X, d2Y, d2Z);
	}

	private static double sqr(double d) {
		return d * d;
	}

	@Override
	public Vector getVelocityLaplacian(Vector position) {
		Vector dvdx = getVelocity2ndDerivative(position, Direction.X);
		Vector dvdy = getVelocity2ndDerivative(position, Direction.Y);
		Vector dvdz = getVelocity2ndDerivative(position, Direction.Z);
		Matrix m = new Matrix(dvdx, dvdy, dvdz);
		return m.sumColumnVectors();
	}

	@Override
	public double getPressureCorrectiveFunction(Vector position) {
		// solve del2 p + del.(v.del)v=0
		// i.e del2 p + div (J(v)v) = 0
		Value value = getValue(position);
		Value wallValue = new Value(new Vector(0, 0, 0), value.pressure,
				value.depth, value.density, value.viscosity);
		Neighbours n = getNeighbours(position, wallValue);
		double pressureLaplacian = getPressureLaplacian(position);

		Vector resultX = f(n.x1, n.x2, n.valueX1.velocity, n.valueX2.velocity);
		Vector resultY = f(n.y1, n.y2, n.valueY1.velocity, n.valueY2.velocity);
		Vector resultZ = f(n.z1, n.z2, n.valueZ1.velocity, n.valueZ2.velocity);

		return pressureLaplacian + resultX.add(resultY).add(resultZ).sum();
	}

	private Vector f(Vector v1, Vector v2, Vector velocity1, Vector velocity2) {
		Vector f2 = getVelocityJacobian(v2).multiply(velocity2);
		Vector f1 = getVelocityJacobian(v1).multiply(velocity1);
		Vector result = f2.minus(f1).divide(v2.minus(v1));
		return result;
	}

	private double getPressureLaplacian(Vector position) {
		Vector d = getPressure2ndDerivative(position);
		return d.sum();
	}

}
