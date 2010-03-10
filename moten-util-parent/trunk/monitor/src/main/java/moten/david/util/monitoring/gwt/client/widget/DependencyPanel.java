package moten.david.util.monitoring.gwt.client.widget;

import moten.david.util.monitoring.gwt.client.check.AppCheckResult;
import moten.david.util.monitoring.gwt.client.check.AppDependency;

import com.google.gwt.user.client.ui.VerticalPanel;

public class DependencyPanel extends VerticalPanel {
	public DependencyPanel(AppDependency dependency, AppCheckResult[] results) {
		CheckPanel checkPanel = new CheckPanel(dependency.getCheck(), results);
		add(checkPanel);
	}
}
