package moten.david.util.monitoring.gwt.client;

import moten.david.util.monitoring.gwt.client.widget.AppPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class ApplicationClient implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel.get("container").add(new AppPanel());
	}
}