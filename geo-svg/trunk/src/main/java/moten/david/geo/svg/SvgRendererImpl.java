package moten.david.geo.svg;

import org.apache.commons.lang.StringEscapeUtils;

public class SvgRendererImpl implements SvgRenderer {

    @Override
    public String line(ScreenPoint a, ScreenPoint b, String style) {
	return "\n<line x1=\"" + a.getX() + "\" y1=\"" + a.getY() + "\" x2=\""
		+ b.getX() + "\" y2=\"" + b.getY() + "\" style=\""
		+ (style == null ? "" : style) + "\"/>";
    }

    @Override
    public String point(ScreenPoint point, double radius, String colour) {
	return "\n<circle cx=\"" + point.getX() + "\" cy=\"" + point.getY()
		+ "\" r=\"" + radius
		+ "\" stroke=\"black\" stroke-width=\"1\" fill=\"" + colour
		+ "\"/>";
    }

    @Override
    public String header(int width, int height) {
	return "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\""
		+ width + "\" height=\"" + height + "\" version=\"1.1\">";
    }

    @Override
    public String image(ScreenPoint point, int width, int height, String url) {
	return "\n<image x=\"" + point.getX() + "\" y=\"" + point.getY()
		+ "\"  width=\"" + width + "\" height=\"" + height
		+ "\" xlink:href=\"" + StringEscapeUtils.escapeXml(url)
		+ "\"/>";
    }

}
