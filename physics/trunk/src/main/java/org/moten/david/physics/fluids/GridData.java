package org.moten.david.physics.fluids;

import org.moten.david.physics.fluids.Vector.Direction;
import org.moten.david.util.math.Function;

public class GridData implements Data {

	@Override
	public Function<Vector, Double> getField(Direction direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Vector, Double> getPressure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WallFinder getWallFinder(Direction direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Vector, Double> getDensity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Vector, Double> getDynamicViscosity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Vector, Vector> getVelocityField() {
		// TODO Auto-generated method stub
		return null;
	}

}
