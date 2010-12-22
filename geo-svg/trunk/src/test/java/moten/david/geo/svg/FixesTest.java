package moten.david.geo.svg;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import moten.david.util.io.IoUtil;

import org.junit.Test;

public class FixesTest {
    @Test
    public void testLotsOfDots() throws IOException {
	SvgGenerator g = SvgGeneratorTest.createSvgGenerator();
	FixParser parser = new FixParser();
	for (String line : IoUtil.getLines(getClass().getResourceAsStream(
		"/fixes.txt"))) {
	    System.out.println(line);

	    if (line.trim().length() > 0) {
		Fix fix = parser
			.parse(new ByteArrayInputStream(line.getBytes()));
		g.add(new Point(fix.getLon(), fix.getLat()));
	    }
	}
	FileOutputStream fos = new FileOutputStream("target/test2.svg");
	fos.write(g.svg().getBytes());
	fos.close();
    }
}
