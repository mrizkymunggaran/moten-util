package moten.david.markup.events;

import moten.david.markup.SelectionMode;
import moten.david.util.controller.Event;

public class SelectionModeChanged implements Event {

	private final SelectionMode selectionMode;

	public SelectionModeChanged(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}
}
