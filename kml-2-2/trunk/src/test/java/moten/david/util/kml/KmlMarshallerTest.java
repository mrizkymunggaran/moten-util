package moten.david.util.kml;

import java.math.BigDecimal;

import moten.david.opengis.kml.v_2_2_0.KmlType;
import moten.david.util.kml.KmlMarshaller;
import moten.david.util.kml.PositionsToKmlConverter;

import org.junit.Assert;
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
        Assert
                .assertEquals(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:ns2=\"http://www.google.com/kml/ext/2.2\" xmlns:ns3=\"http://www.w3.org/2005/Atom\" xmlns:ns4=\"urn:oasis:names:tc:ciq:xsdschema:xAL:2.0\">\n"
                                + "    <Document>\n"
                                + "        <name>My Points</name>\n"
                                + "        <Placemark>\n"
                                + "            <name>VesselA</name>\n"
                                + "            <description>&lt;html&gt;&lt;b&gt;VesselA&lt;/b&gt;&lt;/html&gt;</description>\n"
                                + "            <Point>\n"
                                + "                <coordinates>123.45,-33.77,0</coordinates>\n"
                                + "            </Point>\n"
                                + "        </Placemark>\n"
                                + "    </Document>\n" + "</kml>\n", k
                                .replaceAll("\r", ""));
    }

    @Test
    public void testNoPointsProducesValidEmptyKml() {
        PositionsToKmlConverter convertor = new PositionsToKmlConverter(
                "My Points");
        KmlType kml = convertor.getKmlType();
        KmlMarshaller m = new KmlMarshaller();
        String k = m.getKmlAsString(kml, true);

        System.out.println(k);
        Assert
                .assertEquals(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:ns2=\"http://www.google.com/kml/ext/2.2\" xmlns:ns3=\"http://www.w3.org/2005/Atom\" xmlns:ns4=\"urn:oasis:names:tc:ciq:xsdschema:xAL:2.0\">\n"
                                + "    <Document>\n"
                                + "        <name>My Points</name>\n"
                                + "    </Document>\n" + "</kml>\n", k
                                .replaceAll("\r", ""));
    }
}
