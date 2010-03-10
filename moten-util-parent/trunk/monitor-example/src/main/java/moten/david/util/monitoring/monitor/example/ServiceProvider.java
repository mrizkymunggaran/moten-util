package moten.david.util.monitoring.monitor.example;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.server.ApplicationServiceProvider;

public class ServiceProvider implements ApplicationServiceProvider {

	public ApplicationService get() {
		return new Service();
	}

}
