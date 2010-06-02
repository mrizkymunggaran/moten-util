package moten.david.markup.events;

import moten.david.util.controller.Event;

public class DocumentSelectionChanged implements Event {
    private final int index;

    public int getIndex() {
        return index;
    }

    public DocumentSelectionChanged(int index) {
        super();
        this.index = index;
    }
}
