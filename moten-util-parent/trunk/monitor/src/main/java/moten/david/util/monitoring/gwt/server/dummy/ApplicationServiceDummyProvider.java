package moten.david.util.monitoring.gwt.server.dummy;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.server.ApplicationServiceProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ApplicationServiceDummyProvider implements
		ApplicationServiceProvider {


	private final Injector injector;

	public ApplicationServiceDummyProvider() {
		injector = Guice.createInjector(new InjectorModule());
	}
	
	@Override
	public ApplicationService get() {
		return injector.getInstance(ApplicationService.class);
	}
}
