package org.moten.david.util.xsd.simplified;

import javax.xml.namespace.QName;

public class XsdInteger implements XsdType<Integer> {

	private final Integer value;

	public XsdInteger(Integer value) {
		super();
		this.value = value;
	}

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
