package moten.david.markup;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import moten.david.markup.xml.study.ObjectFactory;
import moten.david.markup.xml.study.Study;

import org.xml.sax.SAXException;

public class StudyMarshaller {

	private Unmarshaller unmarshaller;

	public StudyMarshaller() {
		try {
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = context.createUnmarshaller();
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(getClass().getResource(
					"/study.xsd"));
			unmarshaller.setSchema(schema);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public Study unmarshal(InputStream is) {
		try {
			JAXBElement<Study> res = (JAXBElement<Study>) unmarshaller
					.unmarshal(new StreamSource(is));
			return res.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
