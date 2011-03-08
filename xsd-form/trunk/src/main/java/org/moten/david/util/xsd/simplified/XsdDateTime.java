package org.moten.david.util.xsd.simplified;

import java.util.Calendar;

import javax.xml.namespace.QName;

public class XsdDateTime implements XsdType<Calendar> {

	private final Calendar value;

	public XsdDateTime(Calendar calendar) {
		this.value = calendar;
	}

	@Override
	public Calendar getValue() {
		return value;
	}

	private static final QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE,
			"date");

	@Override
	public QName getQName() {
		return qName;
	}

}
