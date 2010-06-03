package moten.david.markup.events;

import java.awt.Color;

import moten.david.markup.xml.study.Tag;
import moten.david.util.controller.Event;

public class PaintingTag implements Event {

    private final Tag tag;
    private final Color color;
    private final int startY;

    public Tag getTag() {
        return tag;
    }

    public Color getColor() {
        return color;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndY() {
        return endY;
    }

    private final int endY;

    public PaintingTag(Tag tag, Color color, int startY, int endY) {
        super();
        this.tag = tag;
        this.color = color;
        this.startY = startY;
        this.endY = endY;
    }

}
