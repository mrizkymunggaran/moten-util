package org.moten.david.util.xsd.simplified;

import javax.xml.namespace.QName;

public class XsdString implements XsdType<String> {

	private final String value;

	public XsdString(String value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	private static final QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE,
			"string");

	@Override
	public QName getQName() {
		return qName;
	}

}
