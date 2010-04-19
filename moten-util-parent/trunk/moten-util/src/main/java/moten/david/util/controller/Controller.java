package moten.david.util.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

/**
 * Acts as an event bus. Notifies registered listeners of events.
 * 
 * @author Dave Moten
 * 
 */
@Singleton
public class Controller {
	private final Map<Class<? extends Event>, List<ControllerListener<? extends Event>>> map = new HashMap<Class<? extends Event>, List<ControllerListener<? extends Event>>>();

	/**
	 * Notifies the controller that an event occurred. The controller will then
	 * notify registered listeners interested in that type of event.
	 * 
	 * @param event
	 */
	public void event(Event event) {
		for (Class<? extends Event> cls : map.keySet()) {
			if (cls.isInstance(event)) {
				for (ControllerListener listener : map.get(cls)) {
					listener.event(event);
				}
			}
		}
	}

	/**
	 * Adds a listener for events of type cls. Also will be notified of any
	 * events that are subclasses of cls.
	 * 
	 * @param <T>
	 * @param cls
	 * @param listener
	 */
	public synchronized <T extends Event> void addListener(Class<T> cls,
			ControllerListener<T> listener) {
		if (map.get(cls) == null)
			map.put(cls, new ArrayList<ControllerListener<? extends Event>>());
		map.get(cls).add(listener);
	}

	/**
	 * Returns true if and only if the listener was registered with the
	 * controlller and was successfully removed.
	 * 
	 * @param <T>
	 * @param cls
	 * @param listener
	 * @return
	 */
	public synchronized <T extends Event> boolean removeListener(Class<T> cls,
			ControllerListener<T> listener) {
		if (map.get(cls) == null)
			return false;
		else
			return map.get(cls).remove(listener);
	}
}
