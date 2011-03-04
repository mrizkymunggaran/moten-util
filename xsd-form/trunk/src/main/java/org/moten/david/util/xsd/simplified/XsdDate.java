package org.moten.david.util.xsd.simplified;

import java.util.Calendar;

import javax.xml.namespace.QName;

public class XsdDate implements XsdType<Calendar> {

	private Calendar value;

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
