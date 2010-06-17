package moten.david.util.monitoring.gwt.client.widget;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AppPanel extends VerticalPanel {

    public AppPanel() {
        setStyleName("app");
        add(new TitlePanel());
        HorizontalPanel menuAndBody = new HorizontalPanel();
        add(menuAndBody);
        menuAndBody.add(new MenuPanel());
        menuAndBody.add(new BodyPanel());
    }

}