package moten.david.util.monitoring.gwt.client.widget;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.ApplicationServiceAsync;
import moten.david.util.monitoring.gwt.client.check.AppCheck;
import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class BodyPanel extends VerticalFlowPanel {

	ApplicationServiceAsync service = (ApplicationServiceAsync) GWT
			.create(ApplicationService.class);

	public BodyPanel() {
		setStyleName("bodyPanel");
		service.getResults(createResultsCallback());
	}

	private AsyncCallback<AppChecks> createResultsCallback() {
		return new AsyncCallback<AppChecks>() {

			@Override
			public void onFailure(Throwable t) {
				add(new HTML(t.getMessage()));
			}

			@Override
			public void onSuccess(AppChecks checks) {
				if (checks != null) {
					if (checks.getChecks() != null)
						for (AppCheck c : checks.getChecks()) {
							add(new CheckPanel(c, checks.getResults()));
						}
				}
			}

		};
	}
}
