package moten.david.util.monitoring.monitor.example;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.check.AppChecks;

public class Service implements ApplicationService {

	public void check() {
		throw new RuntimeException("not implemented");
	}

	public String getApplicationName() {
		return "service";
	}

	public AppChecks getResults() {
		return null;
	}

}
