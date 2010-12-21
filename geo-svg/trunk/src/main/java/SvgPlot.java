import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

public class SvgPlot {
	private final List<Point> points = new ArrayList<Point>();
	private final double radius;
	private final String colour;
	private final int widthPixels;
	private final int heightPixels;
	private final Function<Point, ScreenPoint> projector;

	public SvgPlot(double radius, String colour, int widthPixels,
			int heightPixels, Point topLeft,
			Function<Point, ScreenPoint> projector) {
		this.radius = radius;
		this.colour = colour;
		this.widthPixels = widthPixels;
		this.heightPixels = heightPixels;
		this.projector = projector;
	}

	public SvgPlot addPoint(double x, double y) {
		points.add(new Point(x, y));
		return this;
	}

	public String svg() {
		StringBuffer s = new StringBuffer();
		s.append("<?xml version=\"1.0\" standalone=\"no\"?>\n"
				+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n"
				+ "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
				+ "\n" + "<svg width=\"" + widthPixels + "\" height=\""
				+ heightPixels + "\" version=\"1.1\"\n"
				+ "xmlns=\"http://www.w3.org/2000/svg\">");
		for (Point point : points) {
			ScreenPoint sp = projector.apply(point);
			s.append("<circle cx=\"" + sp.getX() + "\" cy=\"" + sp.getY()
					+ "\" r=\"" + radius
					+ "\" stroke=\"black\" stroke-width=\"1\" fill=\"" + colour
					+ "\"/>");
		}
		s.append("</svg>");
		return s.toString();
	}
}
