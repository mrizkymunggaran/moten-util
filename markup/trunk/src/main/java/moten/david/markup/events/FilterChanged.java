package moten.david.markup.events;

import moten.david.util.controller.Event;

public class FilterChanged implements Event {

	private final boolean enabled;

	public FilterChanged(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
