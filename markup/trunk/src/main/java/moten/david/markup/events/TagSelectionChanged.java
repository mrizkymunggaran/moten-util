package moten.david.markup.events;

import java.util.List;

import moten.david.util.controller.Event;

public class TagSelectionChanged implements Event {
    private final List<String> list;

    public TagSelectionChanged(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }
}
