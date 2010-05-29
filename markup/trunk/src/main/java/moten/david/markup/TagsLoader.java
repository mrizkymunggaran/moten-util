package moten.david.markup;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moten.david.markup.events.TagsChanged;
import moten.david.util.controller.Controller;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;

public class TagsLoader {
    private final Controller controller;

    @Inject
    public TagsLoader(Controller controller) {
        this.controller = controller;

    }

    public void load() {
        List<String> list;
        try {
            list = IOUtils.readLines(getClass()
                    .getResourceAsStream("/tags.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Tag> tags = new ArrayList<Tag>();
        for (String line : list) {
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                String[] items = line.split("\t");
                String name = items[0];
                Color colour = Color.decode("0x" + items[1]);
                tags.add(new Tag(name, colour));
            }
        }
        controller.event(new TagsChanged(tags));
    }
}
