package moten.david.util.monitoring.example;

import static moten.david.util.expression.Util.and;
import static moten.david.util.expression.Util.or;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import moten.david.util.expression.Bool;
import moten.david.util.expression.BooleanExpression;
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.Monitor;

import org.junit.Assert;
import org.junit.Test;

public class CheckTest {

	@Test
	public void test() {
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = Bool.FALSE;
			DefaultCheck check = new DefaultCheck("processing time ok", null,
					e, Level.WARNING, null, null);
			checks.add(check);
			Monitor monitor = new Monitor(checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.WARNING, map.get(check));
		}
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = Bool.TRUE;
			DefaultCheck check = new DefaultCheck("processing time ok", null,
					e, Level.WARNING, null, null);
			checks.add(check);
			Monitor monitor = new Monitor(checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.OK, map.get(check));
		}
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = or(Bool.TRUE, Bool.FALSE);
			final DefaultCheck checkBase = new DefaultCheck(
					"base thing available", null, e, Level.SEVERE, null, null);
			Set<Check> deps = Collections.singleton((Check) checkBase);
			DefaultCheck check = new DefaultCheck("processing time ok", null,
					e, Level.WARNING, deps, null);
			checks.add(check);
			Monitor monitor = new Monitor(checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.OK, map.get(check));
		}
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = and(Bool.FALSE, Bool.TRUE);
			final DefaultCheck checkBase = new DefaultCheck(
					"base thing available", null, e, Level.SEVERE, null, null);
			Set<Check> deps = Collections.singleton((Check) checkBase);
			DefaultCheck check = new DefaultCheck("processing time ok", null,
					e, Level.WARNING, deps, null);
			checks.add(check);
			Monitor monitor = new Monitor(checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.UNKNOWN, map.get(check));
		}
	}
}
