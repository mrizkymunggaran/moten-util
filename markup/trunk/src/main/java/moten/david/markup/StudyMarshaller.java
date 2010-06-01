package moten.david.markup;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import moten.david.markup.xml.study.ObjectFactory;
import moten.david.markup.xml.study.Study;

public class StudyMarshaller {

	private Unmarshaller unmarshaller;

	public StudyMarshaller() {
		try {
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public Study unmarshal(InputStream is) {
		try {
			JAXBElement<Study> res = unmarshaller.unmarshal(
					new StreamSource(is), Study.class);
			return res.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
