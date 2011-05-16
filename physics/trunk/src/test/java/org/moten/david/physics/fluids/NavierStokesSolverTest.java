package org.moten.david.physics.fluids;

import static org.moten.david.physics.fluids.Util.val;

import org.junit.Test;
import org.moten.david.util.math.Vector;

public class NavierStokesSolverTest {

	@Test
	public void test1() {
		NavierStokesSolver s = new NavierStokesSolver();
		Data data = new GridData(new Grid1(val(1000), val(1000), val(1000),
				val(1000), val(1000), val(1000), val(1000)));
		Value val = s.getValueAfterTime(data, new Vector(0, 0, 0), 30 * 60);
		System.out.println(val);
	}

}
