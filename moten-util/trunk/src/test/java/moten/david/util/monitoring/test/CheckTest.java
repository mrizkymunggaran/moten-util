package moten.david.util.monitoring.test;

import static moten.david.util.expression.Util.and;
import static moten.david.util.expression.Util.configuredNum;
import static moten.david.util.expression.Util.configuredTrue;
import static moten.david.util.expression.Util.eq;
import static moten.david.util.expression.Util.gt;
import static moten.david.util.expression.Util.gte;
import static moten.david.util.expression.Util.isNull;
import static moten.david.util.expression.Util.lt;
import static moten.david.util.expression.Util.lte;
import static moten.david.util.expression.Util.neq;
import static moten.david.util.expression.Util.num;
import static moten.david.util.expression.Util.or;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import moten.david.util.expression.Bool;
import moten.david.util.expression.BooleanExpression;
import moten.david.util.expression.Util;
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.Monitor;
import moten.david.util.monitoring.MonitoringLookups;
import moten.david.util.monitoring.lookup.MapLookup;

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

		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("threshold", "20");

			Map<String, String> conf = new HashMap<String, String>();
			conf.put("minimumValue", "23");
			conf.put("enabled", "false");

			MonitoringLookups lookups = new MonitoringLookups();
			lookups.setConfigurationLookup(new MapLookup(conf));
			lookups.setMonitoringLookup(new MapLookup(map));
			Util.setLookups(lookups);

			assertTrue(gt(num(30), num("threshold")));
			assertFalse(lt(num(30), num("threshold")));
			assertTrue(gte(num(30), num("threshold")));
			assertTrue(eq(num(20), num("threshold")));
			assertFalse(eq(num(21), num("threshold")));
			assertFalse(neq(num(20), num("threshold")));
			assertTrue(neq(num(19.2), num("threshold")));
			assertTrue(isNull("not-there"));
			assertFalse(isNull("threshold"));

			assertTrue(eq(num(23), configuredNum("minimumValue")));
			assertFalse(configuredTrue("enabled"));
			conf.put("enabled", "true");
			assertTrue(configuredTrue("enabled"));
		}

		assertTrue(gt(num(3), num(2)));
		assertFalse(gt(num(3), num(3)));
		assertFalse(gt(num(2), num(3)));

		assertTrue(gte(num(3), num(2)));
		assertTrue(gte(num(3), num(3)));
		assertFalse(gte(num(2), num(3)));

		assertFalse(lt(num(3), num(2)));
		assertFalse(lt(num(3), num(3)));
		assertTrue(lt(num(2), num(3)));

		assertFalse(lte(num(3), num(2)));
		assertTrue(lte(num(3), num(3)));
		assertTrue(lte(num(2), num(3)));

	}

	private void assertTrue(BooleanExpression e) {
		Assert.assertTrue(e.evaluate());
	}

	private void assertFalse(BooleanExpression e) {
		Assert.assertFalse(e.evaluate());
	}
}
