package moten.david.geo.svg;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;

public class FixParser {

    public Fix parse(InputStream is) {

	try {
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder();
	    Document doc = builder.parse(is);
	    Element root = doc.getDocumentElement();

	    NodeList nodes = doc.getElementsByTagName("identity");
	    ImmutableMap.Builder<String, String> identities = ImmutableMap
		    .builder();
	    for (int i = 0; i < nodes.getLength(); i++) {
		Element element = (Element) nodes.item(i);
		identities.put(element.getAttribute("name"), element
			.getAttribute("value"));
	    }
	    ImmutableMap.Builder<String, String> properties = ImmutableMap
		    .builder();
	    for (int i = 0; i < nodes.getLength(); i++) {
		Element element = (Element) nodes.item(i);
		properties.put(element.getAttribute("name"), element
			.getAttribute("value"));
	    }
	    DateTime time = ISODateTimeFormat.dateTime().parseDateTime(
		    root.getAttribute("time"));

	    return new Fix(root.getAttribute("agent"), root
		    .getAttribute("type"), Double.parseDouble(root
		    .getAttribute("lat")), Double.parseDouble(root
		    .getAttribute("lon")), time.toDate(), identities.build(),
		    properties.build(), null, null);
	} catch (ParserConfigurationException e) {
	    throw new RuntimeException(e);
	} catch (SAXException e) {
	    throw new RuntimeException(e);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
}
