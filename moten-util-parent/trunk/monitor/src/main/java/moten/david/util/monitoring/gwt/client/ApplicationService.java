package moten.david.util.monitoring.gwt.client;

import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface ApplicationService extends RemoteService {
    String getApplicationName();

    void check();

    AppChecks getResults();
}
