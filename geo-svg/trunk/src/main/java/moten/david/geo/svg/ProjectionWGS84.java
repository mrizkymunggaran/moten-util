package moten.david.geo.svg;

public class ProjectionWGS84 implements Projection {

    private final Point topLeft;
    private final double width;
    private final int widthPixels;

    public ProjectionWGS84(Point topLeft, double width, int widthPixels) {
	this.topLeft = topLeft;
	this.width = width;
	this.widthPixels = widthPixels;
    }

    @Override
    public ScreenPoint apply(Point point) {
	int x = (int) (Math.round(((point.getX() - topLeft.getX()) / width)
		* widthPixels));
	int y = (int) (Math.round(((topLeft.getY() - point.getY()) / width)
		* widthPixels));
	return new ScreenPoint(x, y);
    }
}
