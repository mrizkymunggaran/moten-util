package moten.david.util.monitoring.gwt.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BodyPanel extends VerticalFlowPanel {

	public BodyPanel() {
		setStyleName("bodyPanel");
		for (int i = 0; i < 5; i++) {
			add(new Check("container available", "UrlAvailable(container.url)",
					"SEVERE", "SEVERE", Collections
							.singletonList("NOTIFY_ON_CALL"),
					Collections.EMPTY_LIST, list("NOTIFY_ON_CALL",
							"NOTIFY_DEVELOPERS"), list("LOG")));
			add(new Check("container memory", "container.memory.used<1500000",
					"WARNING", "WARNING", Collections.EMPTY_LIST,
					Collections.EMPTY_LIST, Collections.EMPTY_LIST,
					Collections.EMPTY_LIST));
			add(new Check("container available", "UrlAvailable(container.url)",
					"SEVERE", "UNKNOWN", Collections.EMPTY_LIST,
					Collections.EMPTY_LIST, Collections.EMPTY_LIST,
					Collections.EMPTY_LIST));
			add(new Check("cts universal adapter available",
					"UrlAvailable(container.url)", "SEVERE", "OK",
					Collections.EMPTY_LIST, Collections.EMPTY_LIST,
					Collections.EMPTY_LIST, Collections.EMPTY_LIST));
		}
	}

	public <T> List<T> list(T... elements) {
		List<T> list = new ArrayList<T>();
		for (T t : elements)
			list.add(t);
		return list;
	}
}
