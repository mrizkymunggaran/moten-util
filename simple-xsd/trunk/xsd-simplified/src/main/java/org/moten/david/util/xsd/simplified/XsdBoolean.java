package org.moten.david.util.xsd.simplified;

public class XsdBoolean implements XsdType<Boolean> {
	private QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE, "string");

	private boolean value;

	public XsdBoolean(boolean value) {
		this.value = value;
	}

	public XsdBoolean() {

	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public QName getQName() {
		return qName;
	}

	@Override
	public void setValue(Boolean t) {
		value = t;
	}

	@Override
	public void setQName(QName q) {
		qName = q;
	}

}
