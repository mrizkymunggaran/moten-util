package moten.david.markup.events;

import java.util.List;

import moten.david.markup.Tag;
import moten.david.util.controller.Event;

public class TagsChanged implements Event {

    private final List<Tag> tags;

    public TagsChanged(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

}
