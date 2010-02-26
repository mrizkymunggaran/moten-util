package moten.david.util.monitoring.gwt.client.widget;


public class BodyPanel extends VerticalFlowPanel {

    public BodyPanel() {
        setStyleName("bodyPanel");
        for (int i = 0; i < 1; i++) {
            add(new Check("container available", "UrlAvailable(container.url)",
                    "SEVERE", true));
            add(new Check("container memory", "container.memory.used<1500000",
                    "SEVERE", true));
            add(new Check("container available", "UrlAvailable(container.url)",
                    "WARNING", true));
        }
    }

}
