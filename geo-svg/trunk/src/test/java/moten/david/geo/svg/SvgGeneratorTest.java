package moten.david.geo.svg;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

public class SvgGeneratorTest {

    @Test
    public void test() throws IOException {
	int widthPixels = 1200;
	int heightPixels = 800;
	double horizontalExtentDegrees = 70;
	Point topLeft = new Point(100, 0);
	Projection p = new ProjectionWGS84(topLeft, horizontalExtentDegrees,
		widthPixels);
	System.out.println(p.apply(new Point(144, -21)));
	SvgRenderer renderer = new SvgRendererImpl();
	SvgGenerator g = new SvgGenerator(1.8, "red", widthPixels,
		heightPixels, p, renderer);

	// add an image with the assumption that we are using the WGS84
	// projection
	double verticalExtentDegrees = heightPixels
		* (horizontalExtentDegrees / widthPixels);

	g
		.add(new Image(
			new ScreenPoint(0, 0),
			widthPixels,
			heightPixels,
			"http://vmap0.tiles.osgeo.org/wms/vmap0?LAYERS=basic&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&EXCEPTIONS=application/vnd.ogc.se_inimage&FORMAT=image/jpeg&SRS=EPSG:4326&BBOX="
				+ topLeft.getX()
				+ ","
				+ (topLeft.getY() - verticalExtentDegrees)
				+ ","
				+ (topLeft.getX() + horizontalExtentDegrees)
				+ ","
				+ topLeft.getY()
				// + "135,-22,146,-11"
				+ "&WIDTH="
				+ widthPixels
				+ "&HEIGHT="
				+ heightPixels));
	g.add(new Point(144, -21));
	g.add(new Line(new Point(125, -10), new Point(138, -39)));
	FileOutputStream fos = new FileOutputStream("target/test.svg");
	fos.write(g.svg().getBytes());
	fos.close();
    }
}
