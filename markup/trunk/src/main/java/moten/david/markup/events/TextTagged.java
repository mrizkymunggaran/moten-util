package moten.david.markup.events;

import moten.david.markup.Tag;
import moten.david.util.controller.Event;

public class TextTagged implements Event {
    private final Tag tag;

    public Tag getTag() {
        return tag;
    }

    public TextTagged(Tag tag) {
        super();
        this.tag = tag;
    }
}
