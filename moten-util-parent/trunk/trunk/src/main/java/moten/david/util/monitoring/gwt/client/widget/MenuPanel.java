package moten.david.util.monitoring.gwt.client.widget;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MenuPanel extends VerticalPanel {

    public MenuPanel() {

        setStyleName("menuPanel");
        // Button action1 = createMenuItem("Action1");
        // add(action1);
        // Button action2 = createMenuItem("Action2");
        // add(action2);
    }

    private Button createMenuItem(String name) {
        Button button = new Button(name);
        button.setStyleName("menuItem");
        return button;
    }
}
