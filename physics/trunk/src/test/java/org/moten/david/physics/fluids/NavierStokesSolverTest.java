package org.moten.david.physics.fluids;

import static org.junit.Assert.assertEquals;
import static org.moten.david.physics.fluids.Util.val;
import static org.moten.david.util.math.Vector.vector;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;
import org.moten.david.util.math.Direction;
import org.moten.david.util.math.Pair;
import org.moten.david.util.math.Vector;

public class NavierStokesSolverTest {

	private static Logger log = Logger.getLogger(NavierStokesSolverTest.class
			.getName());
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
		assertEquals(0, val.velocity.z, PRECISION);
		System.out.println(N + " calcs in " + t / 1000.0 + "s");
	}

	private Map<Vector, Value> createMap(int n) {
		Map<Vector, Value> map = new HashMap<Vector, Value>();
		for (int i = 1; i <= n; i++)
			for (int j = 1; j <= n; j++)
				for (int k = 0; k <= n; k++) {
					double pressure = 1000.0 * k * 9.8;
					if (i == n || i == 1 || j == 1 || j == n || k == 0
							|| k == n)
						pressure = 0;
					map.put(vector(i, j, -k), val(0, 0, 0, pressure));
				}
		return map;
	}

	@Test
	public void testZeroAnomalousPressureAndVelocityStaysThatWayAfterTime() {
		NavierStokesSolver s = new NavierStokesSolver();
		final int N = 5;
		Map<Vector, Value> map = createMap(N);
		Grid<Value> arrayGrid = new ArrayGrid(map);
		Data data = new GridData(arrayGrid);

		Vector v = vector(4, 4, -4);
		double elapsedTime = 1;// second
		{
			Vector vector = arrayGrid.getNeighbour(v, Direction.Z, false);
			assertEquals(39200, data.getValue(v).pressure, 0.01);
			log.info("vector=" + vector);
			log.info("value=" + data.getValue(vector));
			Value val = s.getValueAfterTime(data, vector, elapsedTime);
			assertEquals(0, val.velocity.z, 0.0001);
			val = s.getValueAfterTime(data, v, elapsedTime);
			assertEquals(0, val.velocity.z, 0.0001);
		}
		for (Pair<Vector, Value> pair : data.getEntries())
			log.info(pair.getA() + "->" + pair.getB());
	}

	@Test
	public void testZeroAnomalousPressureAndOneCellWithXVelocityHasAnEffectAfterTime() {
		NavierStokesSolver s = new NavierStokesSolver();
		final int N = 5;
		Map<Vector, Value> map = createMap(N);
		Grid<Value> arrayGrid = new ArrayGrid(map);
		Data data = new GridData(arrayGrid);

		// Give just one cell some X velocity
		Vector v = vector(3, 3, -3);
		double vPressure = data.getValue(v).pressure;

		// modify map and recreate data
		map.put(v, val(1, 0, 0, vPressure));
		data = new GridData(new ArrayGrid(map));

		double elapsedTime = 60;// second
		{
			Vector vector = arrayGrid.getNeighbour(v, Direction.X, false);
			assertEquals(vPressure, data.getValue(v).pressure, 0.01);
			log.info("vector=" + vector);
			log.info("value=" + data.getValue(vector));
			Value val = s.getValueAfterTime(data, vector, elapsedTime);
			assertEquals(0, val.velocity.x, 0.0001);
			val = s.getValueAfterTime(data, v, elapsedTime);
			assertEquals(0, val.velocity.x, 0.0001);
		}
		for (Pair<Vector, Value> pair : data.getEntries())
			log.info(pair.getA() + "->" + pair.getB());
	}
}
