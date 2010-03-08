package moten.david.util.monitoring.gwt.client.widget;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class TitlePanel extends HorizontalPanel {

    public TitlePanel() {
        setStyleName("titlePanel");
        Label label = new Label("MonitorOne");
        label.setStyleName("title");
        add(label);
    }

}
