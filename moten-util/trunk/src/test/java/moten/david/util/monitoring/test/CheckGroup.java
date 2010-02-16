package moten.david.util.monitoring.test;

import static moten.david.util.monitoring.lookup.LookupType.MONITORING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.Monitor;
import moten.david.util.monitoring.lookup.CachingUrlPropertiesProvider;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.MapLookupFactory;
import moten.david.util.monitoring.lookup.PropertiesLookup;
import moten.david.util.monitoring.lookup.UrlFactory;

import org.junit.Assert;

import com.google.inject.Inject;

/**
 * This test case is a more complete usage example with injected members and
 * represents how monitoring would be used in a production environment. The
 * logic for this example is split between this class and the InjectorModule.
 * 
 * This class is not thread safe if urlPropertiesProvider is shared across
 * multiple instances of this class. Don't make CachingUrlPropertiesProvider a
 * singleton in the InjectorModule.
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

	@Inject
	public CheckGroup(EvaluationContext u,
			CachingUrlPropertiesProvider urlPropertiesProvider,
			UrlFactory urlFactory, MapLookupFactory mapLookupFactory,
			ExpressionPresenter presenter) {

		this.u = u;
		this.urlPropertiesProvider = urlPropertiesProvider;
		this.urlFactory = urlFactory;
		this.mapLookupFactory = mapLookupFactory;
		this.presenter = presenter;

	}

	public void check() {

		// initialize lookups to be used in the checks
		Map<LookupType, Lookup> lookups = new HashMap<LookupType, Lookup>();

		// set configuration lookup
		lookups.put(LookupType.CONFIGURATION, createConfLookup());

		// create some checks

		// initialize the list of checks
		List<Check> checks = new ArrayList<Check>();

		// set monitoring for this check to use /test1.properties as source
		lookups.put(MONITORING, createMonitoringLookup("/test1.properties"));
		// Note that the expression in the constructor below does not refer to a
		// lookup type. The default lookup type is assumed which is set up in
		// InjectorModule.
		DefaultCheck one = new DefaultCheck("one", null, u.eq(u
				.num("num.years"), u.num(40)), lookups, LookupType.MONITORING,
				Level.SEVERE, null, null);
		checks.add(one);

		// add a check
		lookups.put(LookupType.MONITORING,
				createMonitoringLookup("/test2.properties"));
		DefaultCheck two = new DefaultCheck("two", null, u.eq(u
				.num("num.years"), u.num(40)), lookups, LookupType.MONITORING,
				Level.SEVERE, null, null);
		checks.add(two);

		// create a monitor for the checks
		Monitor monitor = new Monitor(u, checks, Level.OK, Level.UNKNOWN);

		{
			// This block would be repeatedly run in a real deployment

			// reset url cache
			urlPropertiesProvider.reset();

			// do the check
			Map<Check, moten.david.util.monitoring.Level> results = monitor
					.check();
			System.out.println(results);
			Assert.assertEquals(Level.SEVERE, results.get(one));
			Assert.assertEquals(Level.OK, results.get(two));
		}

		System.out.println(one.present(presenter));
		System.out.println(two.present(presenter));

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
