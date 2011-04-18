package org.moten.david.util.xsd.simplified;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schema implements Serializable {
	public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	public static final String APPINFO_NAMESPACE = "http://moten.david.org/util/xsd/simplified/appinfo";
	private String namespace;
	private List<ComplexType> complexTypes;
	private List<SimpleType> simpleTypes;
	private List<Element> elements;
	private boolean numberItems = false;

	public boolean getNumberItems() {
		return numberItems;
	}

	public void setNumberItems(boolean numberItems) {
		this.numberItems = numberItems;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setComplexTypes(List<ComplexType> complexTypes) {
		this.complexTypes = complexTypes;
	}

	public void setSimpleTypes(List<SimpleType> simpleTypes) {
		this.simpleTypes = simpleTypes;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public Schema() {

	}

	private Schema(String namespace, List<ComplexType> complexTypes,
			List<SimpleType> simpleTypes, List<Element> elements,
			boolean numberItems) {
		super();
		this.namespace = namespace;
		this.complexTypes = complexTypes;
		this.simpleTypes = simpleTypes;
		this.elements = elements;
		this.numberItems = numberItems;
	}

	@Override
	public String toString() {
		return "Schema [namespace=" + namespace + ", complexTypes="
				+ complexTypes + ", simpleTypes=" + simpleTypes + ", elements="
				+ elements + ", numberItems=" + numberItems + "]";
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

	public static class Builder {
		private final List<ComplexType> complexTypes = new ArrayList<ComplexType>();
		private final List<SimpleType> simpleTypes = new ArrayList<SimpleType>();
		private final List<Element> elements = new ArrayList<Element>();
		private String namespace;
		private boolean numberItems = false;

		public Schema build() {
			return new Schema(namespace, complexTypes, simpleTypes, elements,
					numberItems);
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

		public Builder numberItems(boolean numberItems) {
			this.numberItems = numberItems;
			return this;
		}
	}
}
