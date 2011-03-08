package org.moten.david.util.xsd.simplified;

import java.util.ArrayList;
import java.util.List;

public class Schema {
	public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/XMLSchema";
	private final List<ComplexType> complexTypes = new ArrayList<ComplexType>();
	private final List<SimpleType> simpleTypes = new ArrayList<SimpleType>();
	private final List<Element> elements = new ArrayList<Element>();
	private String namespace;

	private Schema() {

	}

	public static class Builder {
		Schema s = new Schema();

		public Schema build() {
			return s;
		}

		public Builder namespace(String namespace) {
			s.namespace = namespace;
			return this;
		}

		public Builder complexType(ComplexType complexType) {
			s.complexTypes.add(complexType);
			return this;
		}

		public Builder simpleType(SimpleType simpleType) {
			s.simpleTypes.add(simpleType);
			return this;
		}

		public Builder element(Element element) {
			s.elements.add(element);
			return this;
		}
	}
}
