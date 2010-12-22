package moten.david.geo.svg;

public interface SvgRenderer {
    String header(int width, int height);

    String line(ScreenPoint a, ScreenPoint b, String style);

    String point(ScreenPoint point, double radius, String colour);

    String image(ScreenPoint point, int width, int height, String url);
}
