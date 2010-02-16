package moten.david.util.monitoring.test;

import static moten.david.util.monitoring.lookup.LookupType.CONFIGURATION;
import static moten.david.util.monitoring.lookup.LookupType.MONITORING;

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
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.Monitor;
import moten.david.util.monitoring.MonitoringLookups;
import moten.david.util.monitoring.lookup.CachingUrlPropertiesProvider;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.MapLookup;
import moten.david.util.monitoring.lookup.MapLookupFactory;
import moten.david.util.monitoring.lookup.PropertiesLookup;
import moten.david.util.monitoring.lookup.ThreadLocalLookupRecorder;
import moten.david.util.monitoring.lookup.UrlFactory;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CheckTest {

	@Test
	public void test() {
		Injector injector = Guice.createInjector(new InjectorModule());
		EvaluationContext u = injector.getInstance(EvaluationContext.class);
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = Bool.FALSE;
			DefaultCheck check = new DefaultCheck("processing time ok", e,
					Level.WARNING, null, null);
			checks.add(check);
			Monitor monitor = new Monitor(u, checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.WARNING, map.get(check));
		}
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = Bool.TRUE;
			DefaultCheck check = new DefaultCheck("processing time ok", e,
					Level.WARNING, null, null);
			checks.add(check);
			Monitor monitor = new Monitor(u, checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.OK, map.get(check));
		}
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = u.or(Bool.TRUE, Bool.FALSE);
			final DefaultCheck checkBase = new DefaultCheck(
					"base thing available", e, Level.SEVERE, null, null);
			Set<Check> deps = Collections.singleton((Check) checkBase);
			DefaultCheck check = new DefaultCheck("processing time ok", e,
					Level.WARNING, deps, null);
			checks.add(check);
			Monitor monitor = new Monitor(u, checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.OK, map.get(check));
		}
		{
			List<Check> checks = new ArrayList<Check>();
			BooleanExpression e = u.and(Bool.FALSE, Bool.TRUE);
			final DefaultCheck checkBase = new DefaultCheck(
					"base thing available", e, Level.SEVERE, null, null);
			Set<Check> deps = Collections.singleton((Check) checkBase);
			DefaultCheck check = new DefaultCheck("processing time ok", e,
					Level.WARNING, deps, null);
			checks.add(check);
			Monitor monitor = new Monitor(u, checks, Level.OK, Level.UNKNOWN);
			Map<Check, moten.david.util.monitoring.Level> map = monitor.check();
			Assert.assertEquals(Level.UNKNOWN, map.get(check));
		}

		{
			Map<String, String> map = createMap("threshold", "20");
			Map<String, String> conf = createMap("minimumValue", "23",
					"enabled", "false");
			MapLookupFactory factory = injector
					.getInstance(MapLookupFactory.class);

			MonitoringLookups lookups = u.getLookups();
			lookups.put(CONFIGURATION, factory.create(conf));
			lookups.put(MONITORING, factory.create(map));

			assertTrue(u.gt(u.num(30), u.num("threshold")));
			assertFalse(u.lt(u.num(30), u.num("threshold")));
			assertTrue(u.gte(u.num(30), u.num("threshold")));
			assertTrue(u.eq(u.num(20), u.num("threshold")));
			assertFalse(u.eq(u.num(21), u.num("threshold")));
			assertFalse(u.neq(u.num(20), u.num("threshold")));
			assertTrue(u.neq(u.num(19.2), u.num("threshold")));
			assertTrue(u.isNull("not-there"));
			assertFalse(u.isNull("threshold"));

			assertTrue(u.eq(u.num(23), u.num("minimumValue", CONFIGURATION)));
			assertFalse(u.isTrue("enabled", CONFIGURATION));
			conf.put("enabled", "true");
			assertTrue(u.isTrue("enabled", CONFIGURATION));

			map.put("lastRunTimestampMs", "25000");
			assertTrue(u.gt(u.now(), u.date("lastRunTimestampMs")));
		}

		assertTrue(u.gt(u.num(3), u.num(2)));
		assertFalse(u.gt(u.num(3), u.num(3)));
		assertFalse(u.gt(u.num(2), u.num(3)));

		assertTrue(u.gte(u.num(3), u.num(2)));
		assertTrue(u.gte(u.num(3), u.num(3)));
		assertFalse(u.gte(u.num(2), u.num(3)));

		assertFalse(u.lt(u.num(3), u.num(2)));
		assertFalse(u.lt(u.num(3), u.num(3)));
		assertTrue(u.lt(u.num(2), u.num(3)));

		assertFalse(u.lte(u.num(3), u.num(2)));
		assertTrue(u.lte(u.num(3), u.num(3)));
		assertTrue(u.lte(u.num(2), u.num(3)));

	}

	private Map<String, String> createMap(String... items) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < items.length; i += 2) {
			map.put(items[i], items[i + 1]);
		}
		return map;
	}

	@Test
	public void testLookupRecorder() {

		Injector injector = Guice.createInjector(new InjectorModule());

		// use aop to record lookups
		ThreadLocalLookupRecorder lookupRecorder = moten.david.util.monitoring.Util
				.getLookupRecorder();
		lookupRecorder.clear();

		Map<String, String> map = createMap("threshold", "20");

		MapLookupFactory factory = injector.getInstance(MapLookupFactory.class);
		MapLookup lookup = factory.create(map);

		// next call should be recorded via aop
		lookup.get("threshold");

		// check if recorded
		Assert.assertEquals(map, lookupRecorder.getLookups());

		lookupRecorder.clear();

		Assert.assertEquals(Collections.EMPTY_MAP, lookupRecorder.getLookups());

		map.put("name", "fred");

		lookup.get("threshold");
		lookup.get("name");

		// check if recorded
		Assert.assertEquals(map, lookupRecorder.getLookups());

	}

	@Test
	public void testUrlLookup() {

		Injector injector = Guice.createInjector(new InjectorModule());

		// get the configuration lookup
		Lookup confLookup = createConfLookup(injector);

		EvaluationContext u = injector.getInstance(EvaluationContext.class);

		// set up a caching url provider
		CachingUrlPropertiesProvider urlPropertiesProvider = new CachingUrlPropertiesProvider();

		Lookup monitoringLookup = createMonitoringLookup(u,
				urlPropertiesProvider, injector);
		Map<LookupType, Lookup> lookups = new HashMap<LookupType, Lookup>();

		// set lookups
		lookups.put(MONITORING, monitoringLookup);
		lookups.put(LookupType.CONFIGURATION, confLookup);

		// initialize the list of checks
		List<Check> checks = new ArrayList<Check>();

		// add a check
		Check one = new DefaultCheck("one", null, u.eq(u.num("num.years"), u
				.num(25)), lookups, LookupType.MONITORING, Level.SEVERE, null,
				null);
		checks.add(one);

		// add a check
		Check two = new DefaultCheck("two", null, u.eq(u.num("num.years"), u
				.num(24)), lookups, LookupType.MONITORING, Level.SEVERE, null,
				null);
		checks.add(two);

		// create a monitor for the checks
		Monitor monitor = new Monitor(u, checks, Level.OK, Level.UNKNOWN);

		// reset url cache
		urlPropertiesProvider.reset();

		// do the check
		Map<Check, moten.david.util.monitoring.Level> results = monitor.check();
		System.out.println(results);
		Assert.assertEquals(Level.OK, results.get(one));
		Assert.assertEquals(Level.SEVERE, results.get(two));

	}

	private Lookup createMonitoringLookup(EvaluationContext u,
			CachingUrlPropertiesProvider urlPropertiesProvider,
			Injector injector) {

		UrlFactory urlFactory = injector.getInstance(UrlFactory.class);
		PropertiesLookup monitoringLookup = new PropertiesLookup(
				urlPropertiesProvider.getPropertiesProvider(urlFactory,
						"/test1.properties"));
		return monitoringLookup;
	}

	private Lookup createConfLookup(Injector injector) {
		Map<String, String> conf = createMap("minimumValue", "23", "enabled",
				"false");
		MapLookupFactory factory = injector.getInstance(MapLookupFactory.class);
		Lookup confLookup = factory.create(conf);
		return confLookup;
	}

	private void assertTrue(BooleanExpression e) {
		Assert.assertTrue(e.evaluate());
	}

	private void assertFalse(BooleanExpression e) {
		Assert.assertFalse(e.evaluate());
	}
}
