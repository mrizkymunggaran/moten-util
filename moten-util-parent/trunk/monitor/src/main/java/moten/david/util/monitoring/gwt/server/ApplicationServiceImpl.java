package moten.david.util.monitoring.gwt.server;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
        ApplicationService {

    @Override
    public String getApplicationName() {
        return "application";
    }

    @Override
    public void check() {

    }

    @Override
    public AppChecks getResults() {
        return null;
    }

}
