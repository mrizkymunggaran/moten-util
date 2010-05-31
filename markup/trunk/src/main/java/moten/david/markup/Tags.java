package moten.david.markup;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;

public class Tags {

    private final List<Tag> tags;

    @Inject
    public Tags() {
        List<String> list;
        try {
            list = IOUtils.readLines(getClass()
                    .getResourceAsStream("/tags.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> lines = new ArrayList<String>();
        for (String line : list) {
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                lines.add(line);
            }
        }
        tags = new ArrayList<Tag>();
        float s = 1.0f;
        float b = 0.2f;
        float h = 0;
        float step = 1f / lines.size();
        for (String line : lines) {
            String[] items = line.split("\t");
            String name = items[0];
            String type = items[1];
            TagScope scope = TagScope.valueOf(items[2]);
            h += step;
            Color colour = new Color(~Color.HSBtoRGB(h, s, b));
            try {
                tags.add(new Tag(Class.forName(type), name, scope, colour));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Tag> get() {
        return tags;
    }
}
