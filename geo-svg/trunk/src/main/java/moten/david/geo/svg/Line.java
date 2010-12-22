package moten.david.geo.svg;

public class Line implements Shape {
    private final Point a;
    private final Point b;

    public Point getA() {
	return a;
    }

    public Point getB() {
	return b;
    }

    public Line(Point a, Point b) {
	this.a = a;
	this.b = b;
    }
}
