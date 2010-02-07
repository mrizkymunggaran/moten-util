package moten.david.util.monitoring.test;

import static moten.david.util.expression.Util.and;
import static moten.david.util.expression.Util.eq;
import static moten.david.util.expression.Util.gt;
import static moten.david.util.expression.Util.gte;
import static moten.david.util.expression.Util.lt;
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
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.MappedBooleanProvider;
import moten.david.util.monitoring.MappedNumericProvider;
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

		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("threshold", "20");

			MappedBooleanProvider.setMap(map);
			MappedNumericProvider.setMap(map);

			{
				boolean result = gt(num(30), num("threshold")).evaluate();
				Assert.assertTrue(result);
			}
			{
				boolean result = gte(num(30), num("threshold")).evaluate();
				Assert.assertTrue(result);
			}
			{
				boolean result = lt(num(30), num("threshold")).evaluate();
				Assert.assertFalse(result);
			}
			{
				boolean result = eq(num(20), num("threshold")).evaluate();
				Assert.assertTrue(result);
			}
			{
				boolean result = eq(num(21), num("threshold")).evaluate();
				Assert.assertFalse(result);
			}
			{
				boolean result = neq(num(20), num("threshold")).evaluate();
				Assert.assertFalse(result);
			}
			{
				boolean result = neq(num(19.2), num("threshold")).evaluate();
				Assert.assertTrue(result);
			}

		}
	}
}
