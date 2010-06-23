package moten.david.util.kml;

import java.math.BigDecimal;

import moten.david.opengis.kml.v_2_2_0.KmlType;

import org.junit.Test;

public class KmlMarshallerTest {

    @Test
    public void testOnePointsProducesValidKml() {

        PositionsToKmlConverter convertor = new PositionsToKmlConverter(
                "My Points");
        convertor.add(new BigDecimal("-33.77"), new BigDecimal("123.45"),
                "VesselA", "<html><b>VesselA</b></html>", "style");
        KmlType kml = convertor.getKmlType();

        KmlMarshaller m = new KmlMarshaller();
        String k = m.getKmlAsString(kml, true);

        System.out.println(k);
    }

    @Test
    public void testNoPointsProducesValidEmptyKml() {
        PositionsToKmlConverter convertor = new PositionsToKmlConverter(
                "My Points");
        KmlType kml = convertor.getKmlType();
        KmlMarshaller m = new KmlMarshaller();
        String k = m.getKmlAsString(kml, true);

        System.out.println(k);
    }
}
