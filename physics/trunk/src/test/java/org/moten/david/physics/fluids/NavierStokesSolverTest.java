package org.moten.david.physics.fluids;

import static org.moten.david.physics.fluids.Util.val;

import org.junit.Test;
import org.moten.david.util.math.Vector;

public class NavierStokesSolverTest {

	private static Vector origin = new Vector(0, 0, 0);

	@Test
	public void test1() {
		NavierStokesSolver s = new NavierStokesSolver();
		Data data = new GridData(new Grid1(val(10, 10, 0, 1000), val(0, 0, 0,
				1000), val(0, 0, 0, 1000), val(0, 0, 0, 1000), val(0, 0, 0,
				1000), val(0, 0, 0, 1000), val(0, 0, 0, 1000)));
		System.out.println("laplacian=" + data.getVelocityLaplacian(origin));
		Value val = s.getValueAfterTime(data, origin, 30 * 60);
		System.out.println(val);
	}

}
