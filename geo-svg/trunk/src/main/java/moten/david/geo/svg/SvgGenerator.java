package moten.david.geo.svg;

import java.util.ArrayList;
import java.util.List;

public class SvgGenerator {
    private final List<Shape> shapes = new ArrayList<Shape>();
    private final double radius;
    private final String colour;
    private final int width;
    private final int height;
    private final Projection projector;
    private final SvgRenderer renderer;

    public SvgGenerator(double radius, String colour, int width, int height,
	    Projection projector, SvgRenderer renderer) {
	this.radius = radius;
	this.colour = colour;
	this.width = width;
	this.height = height;
	this.projector = projector;
	this.renderer = renderer;
    }

    public SvgGenerator add(Shape shape) {
	shapes.add(shape);
	return this;
    }

    public String svg() {
	StringBuffer s = new StringBuffer();
	s.append(renderer.header(width, height));
	for (Shape shape : shapes) {
	    if (shape instanceof Point) {
		ScreenPoint sp = projector.apply((Point) shape);
		s.append(renderer.point(sp, radius, colour));
	    } else if (shape instanceof Line) {
		Line line = (Line) shape;
		s.append(renderer.line(projector.apply(line.getA()), projector
			.apply(line.getB()),
			"stroke:rgb(99,99,99);stroke-width:2"));
	    } else if (shape instanceof Image) {
		Image image = (Image) shape;
		s.append(renderer.image(image.getTopLeft(), image.getWidth(),
			image.getHeight(), image.getUrl()));
	    } else
		throw new RuntimeException(shape.getClass().getName()
			+ " not implemented by generator");
	}
	s.append("</svg>");
	return s.toString();
    }
}
