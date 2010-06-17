package moten.david.util.controller;

import com.google.inject.Singleton;

/**
 * Acts as an event bus. Notifies registered listeners of events synchronously.
 * 
 * @author Dave Moten
 * 
 */
@Singleton
public class SynchronousController extends ControllerBase {

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

}
