package moten.david.util.monitoring.gwt.client;

import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {
    void getApplicationName(AsyncCallback<String> callback);

    void check(AsyncCallback<Void> callback);

    void getResults(AsyncCallback<AppChecks> callback);
}
