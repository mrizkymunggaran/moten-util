package org.moten.david.util.xsd;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Test;
import org.w3._2001.xmlschema.ObjectFactory;
import org.w3._2001.xmlschema.Schema;
public class MarshallerTest {

	@Test
	public void testUnmarshal() {
		try {
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller u = context.createUnmarshaller();
			StreamSource source = new StreamSource(MarshallerTest.class.getResourceAsStream("/test.xsd"));
			JAXBElement<Schema> element = u.unmarshal(source, Schema.class);
			Schema schema = element.getValue();
			Assert.assertEquals("http://www.amsa.gov.au/craft-position/",  schema.getTargetNamespace());
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
}
