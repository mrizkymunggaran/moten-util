package moten.david.util.kml;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import moten.david.opengis.kml.v_2_2_0.KmlType;
import moten.david.opengis.kml.v_2_2_0.ObjectFactory;

@SuppressWarnings("restriction")
public class KmlMarshaller {

    @SuppressWarnings("restriction")
    private Marshaller marshaller = null;
    private static ObjectFactory factory = new ObjectFactory();

    @SuppressWarnings("restriction")
    private synchronized Marshaller getMarshaller() {
        if (marshaller == null)
            try {
                JAXBContext jc = JAXBContext.newInstance(factory.getClass());
                marshaller = jc.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                        Boolean.TRUE);
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        return marshaller;
    }

    /**
     * Return kml as string in KML 2.2 format.
     * 
     * @param kmlType
     * @param commaDelimitedCoordinates
     *            if and only if this is true, coordinates will be delimited by
     *            a comma. Default is false which corresponds to a delimiter of
     *            whitespace. It appears the Google Earth 5.1 does not honour
     *            the KML 2.2 schema in this regard and requires comma delimited
     *            values in the coordinates element.
     * @return
     */
    @SuppressWarnings("restriction")
    public synchronized String getKmlAsString(KmlType kmlType,
            boolean commaDelimitedCoordinates) {
        JAXBElement<KmlType> element = factory.createKml(kmlType);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            getMarshaller().marshal(element, bytes);
            String s = bytes.toString();
            // coordinates must be comma delimited for google earth to
            // understand it!!!!
            if (commaDelimitedCoordinates)
                s = s.replaceAll(
                        "<coordinates>(\\S+)\\s(\\S+)\\s+(\\S+)</coordinates>",
                        "<coordinates>$1,$2,$3</coordinates>");
            return s;

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
