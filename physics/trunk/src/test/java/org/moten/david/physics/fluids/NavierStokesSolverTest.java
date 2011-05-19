package org.moten.david.physics.fluids;

import static org.junit.Assert.assertEquals;
import static org.moten.david.physics.fluids.Util.val;
import static org.moten.david.util.math.Vector.vector;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
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

	private Map<Vector, Value> createMap2(int n) {
		Map<Vector, Value> map = new HashMap<Vector, Value>();
		for (int i = 1; i <= n; i++)
			for (int j = 1; j <= n; j++)
				for (int k = 0; k <= n; k++) {
					double pressure = 1000.0 * k * 9.8;
					if ((i == 3 && j == 3) || (i == 2 && j == 3))
						pressure += 1000.5;
					if (i == n || i == 1 || j == 1 || j == n || k == 0
							|| k == n)
						pressure = 0;
					map.put(vector(i, j, -k), val(0, 0, 0, pressure));
				}
		return map;
	}

	private Map<Vector, Value> createMap3(int n) {
		Map<Vector, Value> map = new TreeMap<Vector, Value>();
		for (int i = 1; i <= n; i++)
			for (int j = 1; j <= n; j++)
				for (int k = 0; k <= n; k++) {
					double pressure = 1000.0 * k * 9.8;
					if ((i >= 3 && i <= 5 && j >= 3 && j <= 5))
						pressure += 100.5;// 10cm of anomalous sea surface
											// height
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
			log.info(pair.get1() + "->" + pair.get2());
	}

	@Test
	public void testZeroAnomalousPressureAndOneCellWithXVelocityHasAnEffectAfterTime() {
		NavierStokesSolver s = new NavierStokesSolver();
		final int N = 5;
		Map<Vector, Value> map = createMap2(N);
		Grid<Value> arrayGrid = new ArrayGrid(map);
		Data data = new GridData(arrayGrid);
		Vector vector = new Vector(3, 3, -3);
		double elapsedTime = 60;// second
		{
			log.info("vector=" + vector);
			log.info("value=" + data.getValue(vector));
			Value val = s.getValueAfterTime(data, vector, elapsedTime);
			assertEquals(30.015, val.velocity.x, 0.01);
			val = s.getValueAfterTime(data, vector(2, 3, -3), elapsedTime);
			assertEquals(0, val.velocity.x, 0.01);
		}
		log.info("\n" + toString(data, true));

	}

	@Test
	public void testRun() {
		NavierStokesSolver s = new NavierStokesSolver();
		final int N = 10;
		Map<Vector, Value> map = createMap3(N);
		Grid<Value> arrayGrid = new ArrayGrid(map);
		Data data = new GridData(arrayGrid);
		double timeStep = 1;// second
		int numSteps = 25;
		Run run = new Run(data, timeStep, numSteps, createDataLogger());
		run.start();
	}

	private RunListener createDataLogger() {
		return new RunListener() {

			@Override
			public void stepFinished(Data data) {
				log.info("\n------------------------------------------------------------------------\n"
						+ NavierStokesSolverTest.toString(data, true));
			}
		};
	}

	private static String toString(Data data, boolean trimZeroPressure) {
		StringBuilder s = new StringBuilder();
		for (Pair<Vector, Value> pair : data.getEntries()) {
			if (!trimZeroPressure || pair.get2().pressure != 0) {
				s.append(pair.get1());
				s.append("->");
				s.append(pair.get2());
				s.append('\n');
			}
		}
		return s.toString();
	}
}
