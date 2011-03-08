package org.moten.david.util.xsd.form;

import org.junit.Test;
import org.moten.david.util.xsd.Marshaller;
import org.moten.david.util.xsd.simplified.Convertor;
import org.w3._2001.xmlschema.Schema;

public class ConvertorTest {

	@Test
	public void testConvertor() {
		Convertor c = new Convertor();
		Marshaller m = new Marshaller();
		Schema s = m.unmarshal(ConvertorTest.class
				.getResourceAsStream("/test.xsd"));
		org.moten.david.util.xsd.simplified.Schema simple = c.convert(s);
	}
}
