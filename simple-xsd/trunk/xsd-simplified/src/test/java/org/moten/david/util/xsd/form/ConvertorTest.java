package org.moten.david.util.xsd.form;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.moten.david.util.xsd.Convertor;
import org.moten.david.util.xsd.Marshaller;
import org.w3._2001.xmlschema.Schema;

public class ConvertorTest {

	@Test
	public void testConvertor() {
		Convertor c = new Convertor();
		Marshaller m = new Marshaller();
		Schema s = m.unmarshal(ConvertorTest.class
				.getResourceAsStream("/test.xsd"));
		org.moten.david.util.xsd.simplified.Schema simple = c.convert(s);
		assertTrue(simple.getNumberItems());
		System.out.println(simple);
	}

	@Test
	public void testConvertorOnForm63Schema() {
		Convertor c = new Convertor();
		Marshaller m = new Marshaller();
		Schema s = m.unmarshal(ConvertorTest.class
				.getResourceAsStream("/test-complex.xsd"));
		org.moten.david.util.xsd.simplified.Schema simple = c.convert(s);
		System.out.println(simple);
	}
}
