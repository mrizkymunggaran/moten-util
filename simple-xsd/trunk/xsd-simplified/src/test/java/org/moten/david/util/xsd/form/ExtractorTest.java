package org.moten.david.util.xsd.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.junit.Test;
import org.moten.david.util.xsd.Marshaller;
import org.w3._2001.xmlschema.LocalSimpleType;
import org.w3._2001.xmlschema.NoFixedFacet;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.Restriction;
import org.w3._2001.xmlschema.Schema;
import org.w3._2001.xmlschema.TopLevelComplexType;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3._2001.xmlschema.TopLevelSimpleType;

public class ExtractorTest {

	@Test
	public void test() {

		Map<String, TopLevelComplexType> complexTypes = new HashMap<String, TopLevelComplexType>();
		Map<String, TopLevelSimpleType> simpleTypes = new HashMap<String, TopLevelSimpleType>();
		Map<String, TopLevelElement> elements = new HashMap<String, TopLevelElement>();

		Schema schema = new Marshaller().unmarshal(ExtractorTest.class
				.getResourceAsStream("/test.xsd"));
		for (OpenAttrs a : schema.getSimpleTypeOrComplexTypeOrGroup()) {
			if (a instanceof TopLevelComplexType) {
				TopLevelComplexType t = (TopLevelComplexType) a;
				complexTypes.put(t.getName(), t);
			} else if (a instanceof TopLevelSimpleType) {
				TopLevelSimpleType t = (TopLevelSimpleType) a;
				simpleTypes.put(t.getName(), t);
			} else if (a instanceof TopLevelElement) {
				TopLevelElement t = (TopLevelElement) a;
				elements.put(t.getName(), t);
			}
		}
		System.out.println("complextTypes = " + complexTypes);
		System.out.println("simpleTypes = " + simpleTypes);
		System.out.println("elements = " + elements);

		for (TopLevelSimpleType t : simpleTypes.values()) {
			System.out.println(toString(t));
		}
	}

	private static String delimiter = ",";

	private static String toString(TopLevelSimpleType t) {
		return "name=" + t.getName() + delimiter + toString(t.getRestriction());

	}

	private static String toString(Restriction r) {
		String text = "";
		text = append(text, pair("base", r.getBase().toString()));
		text = append(text, pair("simpleType=", toString(r.getSimpleType())));
		text = append(text, pair("enumeration=", toString(r.getFacets())));
		return text;
	}

	private static String append(String text, String s) {
		if (s != null && s.length() > 0)
			if (text.length() > 0)
				return text + delimiter + s;
			else
				return s;
		else
			return text;
	}

	@SuppressWarnings("restriction")
	private static String toString(List<Object> facets) {
		StringBuffer s = new StringBuffer();
		for (Object o : facets) {
			if (o instanceof JAXBElement) {
				JAXBElement e = (JAXBElement) o;
				if (e.getName()
						.toString()
						.equals("{http://www.w3.org/2001/XMLSchema}enumeration")
						&& e.getValue() instanceof NoFixedFacet) {
					NoFixedFacet f = (NoFixedFacet) e.getValue();
					if (s.length() > 0)
						s.append(delimiter);
					s.append(f.getValue());
				}
			}
		}
		return s.toString();
	}

	private static String toString(LocalSimpleType simpleType) {
		if (simpleType == null)
			return null;
		else
			return simpleType.getName();
	}

	private static String pair(String name, String value) {
		if (value != null)
			return name + "=" + value;
		else
			return "";
	}
}
