import java.util.ArrayList;
import java.util.List;

public class SvgPlot {
	private final List<Point> points = new ArrayList<Point>();

	public SvgPlot(double radius, String colour, int widthPixels,
			int heightPixels, double left, double top, double width,
			double height) {

	}

	public SvgPlot addPoint(double x, double y) {
		points.add(new Point(x, y));
		return this;
	}

}
