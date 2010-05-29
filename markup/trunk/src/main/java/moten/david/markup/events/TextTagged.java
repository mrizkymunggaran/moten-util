package moten.david.markup.events;

import moten.david.util.controller.Event;

public class TextTagged implements Event {
    private final String tag;

    public String getTag() {
        return tag;
    }

    public TextTagged(String tag) {
        super();
        this.tag = tag;
    }
}
