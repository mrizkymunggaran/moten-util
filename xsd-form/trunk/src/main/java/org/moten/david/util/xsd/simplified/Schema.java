package org.moten.david.util.xsd.simplified;

import java.util.ArrayList;
import java.util.List;

public class Schema {
	public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private final String namespace;

	private Schema(String namespace, List<ComplexType> complexTypes,
			List<SimpleType> simpleTypes, List<Element> elements) {
		super();
		this.namespace = namespace;
		this.complexTypes = complexTypes;
		this.simpleTypes = simpleTypes;
		this.elements = elements;
	}

	@Override
	public String toString() {
		return "Schema [\nnamespace=" + namespace + ", \ncomplexTypes="
				+ complexTypes + ", \nsimpleTypes=" + simpleTypes
				+ ", \nelements=" + elements + "]";
	}

	public String getNamespace() {
		return namespace;
	}

	public List<ComplexType> getComplexTypes() {
		// return a defensive copy
		return new ArrayList<ComplexType>(complexTypes);
	}

	public List<SimpleType> getSimpleTypes() {
		return simpleTypes;
	}

	public List<Element> getElements() {
		return elements;
	}

	private final List<ComplexType> complexTypes;
	private final List<SimpleType> simpleTypes;
	private final List<Element> elements;

	public static class Builder {
		private final List<ComplexType> complexTypes = new ArrayList<ComplexType>();
		private final List<SimpleType> simpleTypes = new ArrayList<SimpleType>();
		private final List<Element> elements = new ArrayList<Element>();
		private String namespace;

		public Schema build() {
			return new Schema(namespace, complexTypes, simpleTypes, elements);
		}

		public Builder namespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder complexType(ComplexType complexType) {
			this.complexTypes.add(complexType);
			return this;
		}

		public Builder simpleType(SimpleType simpleType) {
			this.simpleTypes.add(simpleType);
			return this;
		}

		public Builder element(Element element) {
			this.elements.add(element);
			return this;
		}
	}
}
