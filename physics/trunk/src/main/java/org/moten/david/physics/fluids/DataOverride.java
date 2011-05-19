package org.moten.david.physics.fluids;

import org.moten.david.util.math.Matrix;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

public class DataOverride implements Data {

	private final Data data;
	private final Vector position;
	private final Value value;

	public DataOverride(Data data, Vector position, Value value) {
		this.data = data;
		this.position = position;
		this.value = value;
	}

	@Override
	public Value getValue(Vector position) {
		if (this.position.equals(position))
			return this.value;
		else
			return data.getValue(position);
	}

	@Override
	public Vector getPressureGradient(Vector position) {
		return data.getPressureGradient(position);
	}

	@Override
	public Matrix getVelocityJacobian(Vector position) {
		return data.getVelocityJacobian(position);
	}

	@Override
	public Vector getVelocityLaplacian(Vector position) {
		return data.getVelocityLaplacian(position);
	}

	@Override
	public Iterable<Pair<Vector, Value>> getEntries() {
		return data.getEntries();
	}

	@Override
	public double getPressureCorrectiveFunction(Vector position) {
		return data.getPressureCorrectiveFunction(position);
	}

}
