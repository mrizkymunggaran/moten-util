package moten.david.ets.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import moten.david.ets.client.model.Fix;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Singleton;

@Singleton
public class FixesMarshaller {

    private final DatatypeFactory datatypeFactory;

    @SuppressWarnings("restriction")
    public FixesMarshaller() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("restriction")
    public List<MyFix> unmarshal(InputStream is) {

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList nodes = doc.getElementsByTagName("fix");
            Builder<MyFix> list = ImmutableList.builder();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                com.google.common.collect.ImmutableMap.Builder<String, String> map = ImmutableMap
                        .builder();
                NodeList ids = element.getElementsByTagName("identifier");
                for (int j = 0; j < ids.getLength(); j++) {
                    Element el = (Element) ids.item(j);
                    String name = el.getAttribute("name");
                    String value = el.getAttribute("value");
                    map.put(name, value);
                }
                String id = element.getAttribute("id");
                Double lat = Double.parseDouble(element.getAttribute("lat"));
                Double lon = Double.parseDouble(element.getAttribute("lon"));
                XMLGregorianCalendar x = datatypeFactory
                        .newXMLGregorianCalendar(element.getAttribute("time"));
                Fix f = new Fix();
                f.setId(id);
                f.setLat(lat);
                f.setLon(lon);
                f.setTime(x.toGregorianCalendar().getTime());
                MyFix fix = new MyFix(f, map.build());
                list.add(fix);
            }
            return list.build();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
