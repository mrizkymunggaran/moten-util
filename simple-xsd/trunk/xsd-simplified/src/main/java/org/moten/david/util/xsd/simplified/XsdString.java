package org.moten.david.util.xsd.simplified;

public class XsdString implements XsdType<String> {

	private String value;
	private QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE, "string");

	public QName getqName() {
		return qName;
	}

	public void setqName(QName qName) {
		this.qName = qName;
	}

	public XsdString() {

	}

	public XsdString(String value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public QName getQName() {
		return qName;
	}

	@Override
	public String toString() {
		return "XsdString [value=" + value + "]";
	}

	@Override
	public void setQName(QName q) {
		this.qName = q;
	}

}
