package moten.david.geo.svg;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import moten.david.util.io.IoUtil;

public class Main {
    public static void main(String[] args) throws IOException {
	SvgGenerator g = SvgGeneratorTest.createSvgGenerator(false);
	FixParser parser = new FixParser();
	for (String line : IoUtil.getLines(new FileInputStream(System
		.getProperty("user.home")
		+ "/" + "fixes.txt"))) {
	    System.out.println(line);

	    if (line.trim().length() > 0) {
		Fix fix = parser
			.parse(new ByteArrayInputStream(line.getBytes()));
		g.add(new Point(fix.getLon(), fix.getLat()));
	    }
	}
	FileOutputStream fos = new FileOutputStream("target/test3.svg");
	fos.write(g.svg().getBytes());
	fos.close();
    }
}
