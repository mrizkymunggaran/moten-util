package org.moten.david.util.xsd.simplified;

public class XsdInteger implements XsdType<Integer> {

	private Integer value;
	private QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE, "integer");

	public XsdInteger() {

	}

	public QName getqName() {
		return qName;
	}

	public void setqName(QName qName) {
		this.qName = qName;
	}

	@Override
	public void setValue(Integer value) {
		this.value = value;
	}

	public XsdInteger(Integer value) {
		super();
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public QName getQName() {
		return qName;
	}

	@Override
	public String toString() {
		return "XsdInteger [value=" + value + "]";
	}

	@Override
	public void setQName(QName q) {
		this.qName = q;
	}

}
