package org.moten.david.physics.fluids;

import static org.junit.Assert.assertEquals;
import static org.moten.david.physics.fluids.Util.val;
import static org.moten.david.util.math.Direction.X;
import static org.moten.david.util.math.Direction.Y;
import static org.moten.david.util.math.Direction.Z;
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
		Matrix expected = new Matrix(vector(5.0, 50.0, 500.0), vector(4.5, 45,
				450), vector(4, 40, 400));
		System.out.println(m);
		Assert.assertEquals(expected, m);
	}

	@Test
	public void testVelocity2ndDerivative() {

		GridData data = new GridData(new Grid1(val(100, 100, 100, 1000), val(
				10, 11, 12), val(20, 20, 20), val(100, 110, 120), val(200, 200,
				200), val(1000, 1100, 1200), val(2000, 2000, 2000)));
		{
			Vector v = data.getVelocity2ndDerivative(origin, X);
			System.out.println("2nd derivative dx=" + v);
			assertEquals(-42.5, v.x, PRECISION);
			assertEquals(25, v.y, PRECISION);
			assertEquals(700, v.z, PRECISION);
		}
		{
			Vector v = data.getVelocity2ndDerivative(origin, Y);
			System.out.println("2nd derivative dy=" + v);
			assertEquals(-42.25, v.x, PRECISION);
			assertEquals(27.5, v.y, PRECISION);
			assertEquals(725, v.z, PRECISION);
		}
		{
			Vector v = data.getVelocity2ndDerivative(origin, Z);
			System.out.println("2nd derivative dz=" + v);
			assertEquals(-42.0, v.x, PRECISION);
			assertEquals(30, v.y, PRECISION);
			assertEquals(750, v.z, PRECISION);
		}
	}

	@Test
	public void testVelocityLaplacian() {

		Data data = new GridData(new Grid1(val(100, 100, 100, 1000), val(10,
				11, 12), val(20, 20, 20), val(100, 110, 120),
				val(200, 200, 200), val(1000, 1100, 1200),
				val(2000, 2000, 2000)));
		Vector v = data.getVelocityLaplacian(origin);
		assertEquals(-126.75, v.x, PRECISION);
		assertEquals(82.5, v.y, PRECISION);
		assertEquals(2175, v.z, PRECISION);
		System.out.println("laplacian=" + v);
	}

}
