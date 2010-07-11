package moten.david.util.controller;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import com.google.inject.Inject;

public class AsynchronousController extends ControllerBase {
	private final ExecutorService executorService;
	private static Logger log = Logger.getLogger(AsynchronousController.class
			.getName());

	@Inject
	public AsynchronousController(ExecutorService executorService) {
		this.executorService = executorService;
	}

	/**
	 * Notifies the controller that an event occurred. The controller will then
	 * notify registered listeners interested in that type of event.
	 * 
	 * @param event
	 */
	public void event(final Event event) {
		for (Class<? extends Event> cls : map.keySet()) {
			if (cls.isInstance(event)) {
				for (final ControllerListener listener : map.get(cls)) {
					Runnable r = new Runnable() {
						@Override
						public void run() {
							try {
								listener.event(event);
							} catch (RuntimeException e) {
								e.printStackTrace();
							}
						}
					};
					executorService.execute(r);
				}
			}
		}
	}
}
