package moten.david.geo.svg;

public class Image implements Shape {
    private final ScreenPoint topLeft;
    private final int width;
    private final int height;
    private final String url;

    public ScreenPoint getTopLeft() {
	return topLeft;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public String getUrl() {
	return url;
    }

    public Image(ScreenPoint topLeft, int width, int height, String url) {
	super();
	this.topLeft = topLeft;
	this.width = width;
	this.height = height;
	this.url = url;
    }
}
