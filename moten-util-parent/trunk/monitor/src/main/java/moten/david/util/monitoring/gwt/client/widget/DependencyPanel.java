package moten.david.util.monitoring.gwt.client.widget;

import moten.david.util.monitoring.gwt.client.check.AppCheckResult;
import moten.david.util.monitoring.gwt.client.check.AppDependency;

import com.google.gwt.user.client.ui.VerticalPanel;

public class DependencyPanel extends VerticalPanel {
	public DependencyPanel(AppDependency dependency, AppCheckResult[] results) {
		add(new CheckPanel(dependency.getCheck(), results));
	}
}
