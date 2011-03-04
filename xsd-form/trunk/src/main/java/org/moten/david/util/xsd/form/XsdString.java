package org.moten.david.util.xsd.form;

import javax.xml.namespace.QName;

public class XsdString implements XsdType<String> {

	private String value;

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
