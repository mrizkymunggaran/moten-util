package org.moten.david.util.xsd.simplified;

import java.util.Date;

public class XsdDateTime implements XsdType<Date> {

	private QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE, "date");
	// would prefer to use calendar but gwt client does not support
	private Date value;

	@Override
	public void setQName(QName qName) {
		this.qName = qName;
	}

	public XsdDateTime() {

	}

	@Override
	public void setValue(Date value) {
		this.value = value;
	}

	public XsdDateTime(Date calendar) {
		this.value = calendar;
	}

	@Override
	public Date getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "XsdDateTime [value=" + value + "]";
	}

	@Override
	public QName getQName() {
		return qName;
	}

}
