package moten.david.geo.svg;

public class Point implements Shape {
    private final double x;
    private final double y;
    private final double z;

    public Point(double x, double y, double z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public Point(double x, double y) {
	this(x, y, 0);
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    public double getZ() {
	return z;
    }
}
