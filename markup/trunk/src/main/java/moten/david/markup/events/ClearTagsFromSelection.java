package moten.david.markup.events;

import moten.david.util.controller.Event;

public class ClearTagsFromSelection implements Event {

	private final int position;
	private final int length;

	public ClearTagsFromSelection(int position, int length) {
		this.position = position;
		this.length = length;
	}

	public int getPosition() {
		return position;
	}

	public int getLength() {
		return length;
	}

}
