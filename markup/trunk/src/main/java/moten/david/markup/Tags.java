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
		tags = new ArrayList<Tag>();
		List<String> names = new ArrayList<String>();
		for (String line : list) {
			line = line.trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				String[] items = line.split("\t");
				String name = items[0];
				Color colour = Color.decode("0x" + items[1]);

				tags.add(new Tag(name, colour));
				names.add(name);
			}
		}
		tags.clear();
		float s = 1;
		float b = 0.95f;
		float h = 0;
		float step = 1f / names.size();

		for (String name : names) {
			h += step;
			Color colour = new Color(Color.HSBtoRGB(h, s, b));
			tags.add(new Tag(name, colour));
		}

	}

	public List<Tag> get() {
		return tags;
	}
}
