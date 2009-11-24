package moten.david.util.tv.ui.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

	private final Map<Class<? extends Event>, List<ControllerListener<? extends Event>>> map = new HashMap<Class<? extends Event>, List<ControllerListener<? extends Event>>>();

	public void event(Event event) {
		for (Class<? extends Event> cls : map.keySet()) {
			if (cls.getName().equals(event.getClass().getName())) {
				for (ControllerListener listener : map.get(cls)) {
					listener.event(event);
				}
			}
		}
	}

	public <T extends Event> void addListener(Class<T> cls,
			ControllerListener<T> listener) {
		if (map.get(cls) == null)
			map.put(cls, new ArrayList<ControllerListener<? extends Event>>());
		map.get(cls).add(listener);
	}
}
