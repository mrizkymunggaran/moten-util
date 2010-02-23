package moten.david.util.monitoring.gwt.client.widget;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class TitlePanel extends HorizontalPanel {

	public TitlePanel() {
		setStyleName("title");
		add(new Label("Monitor"));
	}

}
