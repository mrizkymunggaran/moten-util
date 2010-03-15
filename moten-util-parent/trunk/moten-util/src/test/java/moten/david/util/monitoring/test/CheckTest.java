package moten.david.util.monitoring.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.CheckResult;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.lookup.Lookups;
import moten.david.util.monitoring.lookup.UrlLookup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;

public class CheckTest {

	@Inject
	private UrlLookup urlLookup;
	@Inject
	private ConfigurationLookup configurationLookup;

	@Before
	public void init() {
		Guice.createInjector(new InjectorModule()).injectMembers(this);
	}

	@Test
	public void test() throws IOException {

		Lookups lookups = new Lookups();
		lookups.put(MyLookupType.APPLICATION, urlLookup);
		lookups.put(MyLookupType.CONFIGURATION, configurationLookup);

		EvaluationContext context = new EvaluationContext(
				MyLookupType.APPLICATION, lookups);

		DefaultCheck check1 = new DefaultCheck("test url lookup",
				"does a test using a url properties lookup", context
						.isTrue("enabled"), context, getClass().getResource(
						"/test1.properties").toString(), Level.SEVERE, null,
				null);

		DefaultCheck check2 = new DefaultCheck("test url lookup 2",
				"does a test using a url properties lookup", context.gte(
						context.num("num.years"), context.num(20)), context,
				getClass().getResource("/test1.properties").toString(),
				Level.SEVERE, null, null);

		DefaultCheck check3 = new DefaultCheck("test url lookup 3",
				"does a test using a url properties lookup", context.gte(
						context.num("num.years"), context.num(40)), context,
				getClass().getResource("/test1.properties").toString(),
				Level.WARNING, null, null);

		// find a free server socket
		ServerSocket s = new ServerSocket(0);
		int port = s.getLocalPort();
		s.close();

		System.out.println(port);

		DefaultCheck check4 = new DefaultCheck("localhost socket", "", context
				.socketAvailable("localhost", port), context, (String) null,
				Level.SEVERE, null, null);

		DefaultCheck check5 = new DefaultCheck("google search is available",
				"", context.urlAvailable("http://localhost:" + port), context,
				(String) null, Level.WARNING, null, null);

		Checker checker = new Checker(Level.OK, Level.UNKNOWN, Level.EXCEPTION,
				check1, check2, check3, check4, check5);

		Map<Check, CheckResult> results = checker.check();

		Assert.assertEquals(Level.OK, results.get(check1).getLevel());
		Assert.assertEquals(Level.OK, results.get(check2).getLevel());
		Assert.assertEquals(Level.WARNING, results.get(check3).getLevel());
		Assert.assertEquals(Level.SEVERE, results.get(check4).getLevel());
		Assert.assertEquals(Level.WARNING, results.get(check5).getLevel());

	}
}
