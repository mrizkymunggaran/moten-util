package moten.david.util.kml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import moten.david.opengis.kml.v_2_2_0.AbstractFeatureType;
import moten.david.opengis.kml.v_2_2_0.DocumentType;
import moten.david.opengis.kml.v_2_2_0.KmlType;
import moten.david.opengis.kml.v_2_2_0.ObjectFactory;
import moten.david.opengis.kml.v_2_2_0.PlacemarkType;
import moten.david.opengis.kml.v_2_2_0.PointType;

public class PositionsToKmlConverter {

    private final List<Position> positions = new ArrayList<Position>();
    private static ObjectFactory factory = new ObjectFactory();
    private static moten.david.opengis.kml.v_2_2_0.gx.ObjectFactory ogcFactory = new moten.david.opengis.kml.v_2_2_0.gx.ObjectFactory();
    private final String title;

    public PositionsToKmlConverter(String title) {
        this.title = title;
    }

    private static class Position {
        BigDecimal lat;

        public Position(BigDecimal lat, BigDecimal lon, String name,
                String html, String style) {
            super();
            this.lat = lat;
            this.lon = lon;
            this.name = name;
            this.html = html;
            this.style = style;
        }

        BigDecimal lon;
        String name;
        String html;
        String style;
    }

    /**
     * Adds a position. Returns <code>this</code> to facilitate fluent
     * programming.
     * 
     * @param lat
     * @param lon
     * @param name
     * @param html
     * @param icon
     * @return
     */
    public PositionsToKmlConverter add(BigDecimal lat, BigDecimal lon,
            String name, String html, String icon) {
        positions.add(new Position(lat, lon, name, html, icon));
        return this;
    }

    @SuppressWarnings("restriction")
    public KmlType getKmlType() {
        KmlType kmlType = factory.createKmlType();
        kmlType.setAbstractFeatureGroup(getFeatureGroup(positions));

        return kmlType;
    }

    @SuppressWarnings("restriction")
    private JAXBElement<? extends AbstractFeatureType> getFeatureGroup(
            List<Position> positions) {
        DocumentType doc = factory.createDocumentType();
        doc.setName(title);
        for (Position position : positions) {
            PlacemarkType placemark = factory.createPlacemarkType();
            placemark.setName(position.name);
            PointType point = factory.createPointType();
            point.getCoordinates().add(position.lon.toString());
            point.getCoordinates().add(position.lat.toString());
            point.getCoordinates().add("0");
            placemark.setAbstractGeometryGroup(factory.createPoint(point));
            doc.getAbstractFeatureGroup().add(
                    factory.createPlacemark(placemark));
            placemark.setDescription(position.html);
        }
        return factory.createDocument(doc);
    }
}
