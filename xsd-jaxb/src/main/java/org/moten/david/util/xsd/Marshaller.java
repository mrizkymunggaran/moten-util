package org.moten.david.util.xsd;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.w3._2001.xmlschema.ObjectFactory;
import org.w3._2001.xmlschema.Schema;

public class Marshaller {

	private Unmarshaller u;

	public Marshaller() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(ObjectFactory.class);
			u = context.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized Schema unmarshal(InputStream is) {
		try {
			StreamSource source = new StreamSource(is);
			JAXBElement<Schema> element = u.unmarshal(source, Schema.class);
			Schema schema = element.getValue();
			return schema;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
