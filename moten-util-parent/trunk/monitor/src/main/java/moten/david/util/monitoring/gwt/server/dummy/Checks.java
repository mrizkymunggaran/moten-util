package moten.david.util.monitoring.gwt.server.dummy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.lookup.DefaultLevel;
import moten.david.util.monitoring.lookup.DefaultLookupType;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.Lookups;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Checks extends ArrayList<Check> {

	private static final long serialVersionUID = -4842629078057680014L;
	private final Lookup applicationLookup;
	private final Lookup configurationLookup;

	@Inject
	public Checks(@Named("application") Lookup applicationLookup,
			@Named("configuration") Lookup configurationLookup) {
		this.applicationLookup = applicationLookup;
		this.configurationLookup = configurationLookup;
		setup();
	}

	public void setup() {
		Lookups lookups = new Lookups();
		lookups.put(DefaultLookupType.APPLICATION, applicationLookup);
		lookups.put(DefaultLookupType.CONFIGURATION, configurationLookup);

		EvaluationContext context = new EvaluationContext(
				DefaultLookupType.APPLICATION, lookups);

		String properties = "/dummy.properties";

		DefaultCheck check1 = new DefaultCheck("test url lookup",
				"does a test using a url properties lookup", context
						.isTrue("enabled"), context, getClass().getResource(
						properties).toString(), DefaultLevel.SEVERE, null, null);

		add(check1);

		DefaultCheck check2 = new DefaultCheck("test url lookup 2",
				"does a test using a url properties lookup", context.gte(
						context.num("num.years"), context.num(20)), context,
				getClass().getResource(properties).toString(),
				DefaultLevel.SEVERE, null, null);

		add(check2);

		DefaultCheck check3 = new DefaultCheck("test url lookup 3",
				"does a test using a url properties lookup", context.gte(
						context.num("num.years"), context.num(40)), context,
				getClass().getResource(properties).toString(),
				DefaultLevel.WARNING, null, null);

		add(check3);

		int port;
		// find a free server socket
		try {
			ServerSocket s;
			s = new ServerSocket(0);
			port = s.getLocalPort();
			s.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("unused port " + port);

		DefaultCheck check4 = new DefaultCheck("localhost socket", "", context
				.socketAvailable("localhost", port), context, (String) null,
				DefaultLevel.SEVERE, null, null);

		add(check4);

		DefaultCheck check5 = new DefaultCheck("google search is available",
				"", context.urlAvailable("http://localhost:" + port), context,
				(String) null, DefaultLevel.WARNING, null, null);

		add(check5);

		DefaultCheck check6 = new DefaultCheck("script test",
				"does a test using a url properties lookup", context
						.scriptOk("ls -l"), context, getClass().getResource(
						properties).toString(), DefaultLevel.WARNING, null,
				null);

		add(check6);

	}
}
