package moten.david.util.monitoring.test;

import java.util.Map;

import moten.david.util.expression.Bool;
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.CheckResult;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.lookup.LookupParameters;
import moten.david.util.monitoring.lookup.Lookups;
import moten.david.util.monitoring.lookup.UrlLookup;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;

public class CheckGroupTest2 {

	@Inject
	private UrlLookup urlLookup;
	@Inject
	private ConfigurationLookup configurationLookup;

	@Before
	public void init() {
		Guice.createInjector(new InjectorModule()).injectMembers(this);
	}

	@Test
	public void test() {

		Lookups lookups = new Lookups();
		lookups.put(MyLookupType.APPLICATION, urlLookup);
		lookups.put(MyLookupType.CONFIGURATION, configurationLookup);

		EvaluationContext context = new EvaluationContext(
				MyLookupType.APPLICATION, lookups);

		LookupParameters parameters = new LookupParameters(
				MyLookupType.APPLICATION, "classpath://boo");

		DefaultCheck check = new DefaultCheck("test url lookup",
				"does a test using a url properties lookup", new Bool(true),
				context, parameters, Level.SEVERE, null, null);

		Checker checker = new Checker(Level.OK, Level.UNKNOWN, Level.EXCEPTION,
				check);

		Map<Check, CheckResult> results = checker.check();
	}
}
