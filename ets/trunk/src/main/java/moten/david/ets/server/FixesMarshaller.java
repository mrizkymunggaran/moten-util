package moten.david.ets.server;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

    public Iterable<MyFix> unmarshal(InputStream is) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList nodes = doc.getElementsByTagName("fix");
            Builder<MyFix> list = ImmutableList.builder();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                com.google.common.collect.ImmutableMap.Builder<String, String> map = ImmutableMap
                        .builder();
                NodeList ids = element.getElementsByTagName("id");
                for (int j = 0; j < nodes.getLength(); j++) {
                    Element el = (Element) ids.item(j);
                    String name = el.getAttribute("name");
                    String value = el.getAttribute("value");
                    map.put(name, value);
                }

                // MyFix fix = new MyFix();
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
