package org.moten.david.util.xsd.form;

import javax.xml.namespace.QName;

public class XsdInteger implements XsdType<Integer> {

	private Integer value;

	@Override
	public Integer getValue() {
		return value;
	}

	private static final QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE,
			"integer");

	@Override
	public QName getQName() {
		return qName;
	}

}
