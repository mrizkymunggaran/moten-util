package org.moten.david.physics.fluids;

import static org.moten.david.physics.fluids.Testing.val;
import static org.moten.david.util.math.Vector.vector;

import org.junit.Assert;
import org.junit.Test;
import org.moten.david.util.math.Matrix;
import org.moten.david.util.math.Vector;

public class GridDataTest {

	private static final double PRECISION = 0.000001;

	private static Vector origin = vector(0, 0, 0);

	@Test
	public void testPressureGradient() {

		Data data = new GridData(new Grid1(val(1100), val(2000), val(1000),
				val(3000), val(1000), val(4000), val(1000)));
		Assert.assertEquals(val(1100), data.getValue(origin));
		Vector v = data.getPressureGradient(origin);

		Assert.assertEquals(-500, v.x, PRECISION);
		Assert.assertEquals(-1000, v.y, PRECISION);
		Assert.assertEquals(-1500, v.z, PRECISION);
	}

	@Test
	public void testVelocityJacobian() {

		Data data = new GridData(new Grid1(val(1000), val(10, 11, 12), val(20,
				20, 20), val(100, 110, 120), val(200, 200, 200), val(1000,
				1100, 1200), val(2000, 2000, 2000)));
		Matrix m = data.getVelocityJacobian(origin);
		System.out.println(m);
	}

	@Test
	public void testVelocityLaplacian() {

		Data data = new GridData(new Grid1(val(1000), val(10, 11, 12), val(20,
				20, 20), val(100, 110, 120), val(200, 200, 200), val(1000,
				1100, 1200), val(2000, 2000, 2000)));
		Vector v = data.getVelocityLaplacian(origin);
		System.out.println(v);
	}

}
