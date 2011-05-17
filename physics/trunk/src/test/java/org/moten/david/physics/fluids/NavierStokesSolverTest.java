package org.moten.david.physics.fluids;

import static org.junit.Assert.assertEquals;
import static org.moten.david.physics.fluids.Util.val;
import static org.moten.david.util.math.Vector.vector;

import java.util.HashMap;

import org.junit.Test;
import org.moten.david.util.math.Vector;

public class NavierStokesSolverTest {

	private static final double PRECISION = 0.01;
	private static Vector origin = new Vector(0, 0, 0);

	@Test
	public void test1() {
		NavierStokesSolver s = new NavierStokesSolver();
		Data data = new GridData(new Grid1(val(10, 10, 0, 1200),
				val(0, 0, 0, 0), val(0, 0, 0, 0), val(0, 0, 0, 0), val(0, 0, 0,
						0), val(0, 0, 0, 0), val(0, 0, 0, 0)));
		System.out.println("laplacian=" + data.getVelocityLaplacian(origin));
		Value val = s.getValueAfterTime(data, origin, 60);
		long t = System.currentTimeMillis();
		long N = 1000;
		// for (int i = 0; i < N; i++) {
		// s.getValueAfterTime(data, origin, 60);
		// }
		t = System.currentTimeMillis() - t;
		System.out.println(val);
		assertEquals(1200, val.pressure, PRECISION);
		assertEquals(10, val.velocity.x, PRECISION);
		assertEquals(10, val.velocity.y, PRECISION);
		assertEquals(-60 * 9.8, val.velocity.z, PRECISION);
		System.out.println(N + " calcs in " + t / 1000.0 + "s");
	}

	@Test
	public void test2() {
		NavierStokesSolver s = new NavierStokesSolver();
		HashMap<Vector, Value> map = new HashMap<Vector, Value>();
		final int N = 5;
		for (int i = 1; i <= N; i++)
			for (int j = 1; j <= N; j++)
				for (int k = 1; k <= N; k++) {
					double pressure = 1000 * k * 9.8;
					if (i == N || i == 1 || j == 1 || j == N || k == 1
							|| k == N)
						pressure = 0;
					map.put(vector(i, j, -k), val(0, 0, 0, pressure));
				}
		Grid<Value> arrayGrid = new ArrayGrid(map);
		Data data = new GridData(arrayGrid);
		for (int i = 1; i <= N; i++)
			for (int j = 1; j <= N; j++)
				for (int k = 1; k <= N; k++) {
					Vector v = vector(i, j, -k);
					Value val = s.getValueAfterTime(data, v, 1);
					System.out.println(i + "," + j + "," + -k + ": " + val);
				}
	}
}
