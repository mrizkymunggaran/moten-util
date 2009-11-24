package au.edu.anu.delibdem.qsort.gui;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;

public class Status {

	public static void setStatus(String message) {
		EventManager.getInstance().notify(new Event(message,Events.STATUS));
	}
	
	public static void finish() {
		EventManager.getInstance().notify(new Event(null,Events.STATUS_FINISHED));
	}
	
}
