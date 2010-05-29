package moten.david.markup.events;

import java.util.List;

import moten.david.markup.Tag;
import moten.david.util.controller.Event;

public class TagSelectionChanged implements Event {
    private final List<Tag> list;

    public TagSelectionChanged(List<Tag> list) {
        this.list = list;
    }

    public List<Tag> getList() {
        return list;
    }
}
