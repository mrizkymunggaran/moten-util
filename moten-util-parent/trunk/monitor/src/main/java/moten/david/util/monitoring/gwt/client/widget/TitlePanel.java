package moten.david.util.monitoring.gwt.client.widget;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class TitlePanel extends HorizontalPanel {

    public TitlePanel() {
        Image icon = new Image("images/lizard2.png");
        icon.setStyleName("titleIcon");
        add(icon);

        setStyleName("titlePanel");
        Label label = new Label("MonitorOne");
        label.setStyleName("title");
        add(label);
    }
}
