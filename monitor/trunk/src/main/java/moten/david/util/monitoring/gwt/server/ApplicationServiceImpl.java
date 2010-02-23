package moten.david.util.monitoring.gwt.server;

import moten.david.util.monitoring.gwt.client.ApplicationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
		ApplicationService {

	@Override
	public String getApplicationName() {
		return "application";
	}

}
