package org.moten.david.physics.fluids;

import org.junit.Test;
import org.moten.david.util.math.Vector;

public class NavierStokesSolverTest {

	@Test
	public void test1() {
		NavierStokesSolver s = new NavierStokesSolver();
		Data data = new GridData(new Grid1(createValue(0, 0, 0, 1000)));
		s.getValueAfterTime(data, new Vector(0, 0, 0), timeDelta);
	}

	public Value createValue(double xSpeed, double ySpeed, double zSpeed,
			double pressure) {
		Value value = new Value(new Vector(xSpeed, ySpeed, zSpeed), pressure,
				100, 1000.0, 0.00188);
		return value;
	}

}
