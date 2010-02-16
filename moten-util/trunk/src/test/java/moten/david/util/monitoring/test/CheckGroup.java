package moten.david.util.monitoring.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import moten.david.util.expression.BooleanExpression;
import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.CheckResult;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.Policy;
import moten.david.util.monitoring.lookup.CachingUrlPropertiesProvider;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.MapLookupFactory;
import moten.david.util.monitoring.lookup.PropertiesLookup;
import moten.david.util.monitoring.lookup.UrlFactory;

import org.junit.Assert;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * This test case is a more complete usage example with injected members and
 * represents how monitoring would be used in a production environment. The
 * logic for this example is split between this class and the InjectorModule.
 * 
 * This class is not thread safe if urlPropertiesProvider is shared across
 * multiple instances of this class. Don't make CachingUrlPropertiesProvider a
 * singleton in the InjectorModule.
 * 
 * EvaluationContext is not thread safe. Use a new EvaluationContext per
 * instance of CheckGroup.
 * 
 * @author dxm
 * 
 */
public class CheckGroup {

	private final EvaluationContext u;
	private final CachingUrlPropertiesProvider urlPropertiesProvider;
	private final UrlFactory urlFactory;
	private final MapLookupFactory mapLookupFactory;
	private final ExpressionPresenter presenter;
	private final LookupType lookupTypeDefault;

	@Inject
	public CheckGroup(EvaluationContext u,
			@Named("default") LookupType lookupTypeDefault,
			CachingUrlPropertiesProvider urlPropertiesProvider,
			UrlFactory urlFactory, MapLookupFactory mapLookupFactory,
			ExpressionPresenter presenter) {

		this.u = u;
		this.lookupTypeDefault = lookupTypeDefault;
		this.urlPropertiesProvider = urlPropertiesProvider;
		this.urlFactory = urlFactory;
		this.mapLookupFactory = mapLookupFactory;
		this.presenter = presenter;

	}

	public void check() {

		// initialize lookups to be used in the checks
		Map<LookupType, Lookup> lookups = new HashMap<LookupType, Lookup>();

		// set configuration lookup
		lookups.put(MyLookupType.CONFIGURATION, createConfLookup());

		// create some checks

		// initialize the list of checks
		List<Check> checks = new ArrayList<Check>();

		// add a check - this one should fail
		DefaultCheck one = createUrlDefaultCheck("/test1.properties", "one", u
				.eq(u.num("num.years"), u.num(40)), lookups, Level.SEVERE,
				null, null);
		checks.add(one);

		// add a check - this one should pass
		DefaultCheck two = createUrlDefaultCheck("/test2.properties", "two", u
				.eq(u.num("num.years"), u.num(40)), lookups, Level.SEVERE,
				null, null);
		checks.add(two);

		// add a check - this one should have a null pointer exception on trying
		// to read non existent test372.properties
		DefaultCheck three = createUrlDefaultCheck("/test372.properties",
				"three", u.eq(u.num("num.years"), u.num(40)), lookups,
				Level.SEVERE, null, null);
		checks.add(three);

		// add a check dependent on one. Because one fails this check should
		// return UNKNOWN
		DefaultCheck four = createUrlDefaultCheck("/test372.properties",
				"four", u.eq(u.num("num.years"), u.num(40)), lookups,
				Level.SEVERE, Collections.singleton((Check) one), null);
		checks.add(four);

		// create a monitor for the checks
		Checker checker = new Checker(u, checks, Level.OK, Level.UNKNOWN,
				Level.EXCEPTION);

		{
			// This block would be repeatedly run in a real deployment

			// reset url cache
			urlPropertiesProvider.reset();

			// do the check
			Map<Check, CheckResult> results = checker.check();
			System.out.println(results);
			Assert.assertEquals(Level.SEVERE, results.get(one).getLevel());
			Assert.assertEquals(Level.OK, results.get(two).getLevel());
			Assert.assertEquals(Level.EXCEPTION, results.get(three).getLevel());
			Assert.assertNotNull(results.get(three).getException());
			Assert.assertEquals(Level.UNKNOWN, results.get(four).getLevel());
		}

		System.out.println(one.present(presenter));
		System.out.println(two.present(presenter));

	}

	private DefaultCheck createUrlDefaultCheck(String urlPath, String name,
			BooleanExpression expression, Map<LookupType, Lookup> lookups,
			Level failureLevel, Set<Check> dependencies,
			Set<Policy> failurePolicies) {
		// set monitoring for this check to use urlPath as source
		lookups.put(lookupTypeDefault, createMonitoringLookup(urlPath));
		// Note that the expression in the constructor below does not refer to a
		// lookup type. The default lookup type is assumed which is set up in
		// InjectorModule.
		DefaultCheck check = new DefaultCheck(name, null, expression, lookups,
				failureLevel, dependencies, failurePolicies);
		return check;
	}

	private Map<String, String> createMap(String... items) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < items.length; i += 2) {
			map.put(items[i], items[i + 1]);
		}
		return map;
	}

	private Lookup createMonitoringLookup(String path) {

		PropertiesLookup monitoringLookup = new PropertiesLookup(
				urlPropertiesProvider.getPropertiesProvider(urlFactory, path));
		return monitoringLookup;
	}

	private Lookup createConfLookup() {
		Map<String, String> conf = createMap("minimumValue", "23", "enabled",
				"false");
		Lookup confLookup = mapLookupFactory.create(conf);
		return confLookup;
	}

}
