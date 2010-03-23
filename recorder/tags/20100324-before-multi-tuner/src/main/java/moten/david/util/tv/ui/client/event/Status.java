package moten.david.util.tv.ui.client.event;

import moten.david.util.tv.ui.client.controller.Event;

public class Status implements Event {
	private final String message;

	public String getMessage() {
		return message;
	}

	public Status(String message) {
		super();
		this.message = message;
	}
}
