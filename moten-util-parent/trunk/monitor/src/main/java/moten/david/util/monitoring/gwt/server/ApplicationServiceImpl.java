package moten.david.util.monitoring.gwt.server;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
		ApplicationService {

	private final ApplicationServiceDummy service;

	public ApplicationServiceImpl() {
		service = new ApplicationServiceDummy();
	}

	@Override
	public void check() {
		service.check();

	}

	@Override
	public String getApplicationName() {
		return service.getApplicationName();
	}

	@Override
	public AppChecks getResults() {
		return service.getResults();
	}

}
