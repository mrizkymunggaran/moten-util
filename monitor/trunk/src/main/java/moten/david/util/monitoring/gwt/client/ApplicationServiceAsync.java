package moten.david.util.monitoring.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {
	void getApplicationName(AsyncCallback<String> callback);
}
