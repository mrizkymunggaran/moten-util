package org.moten.david.util.xsd.simplified;

import java.math.BigDecimal;

public class XsdDecimal implements XsdType<BigDecimal> {

	private QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE, "decimal");

	public QName getqName() {
		return qName;
	}

	public void setqName(QName qName) {
		this.qName = qName;
	}

	private BigDecimal value;

	public XsdDecimal() {

	}

	@Override
	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public XsdDecimal(BigDecimal value) {
		super();
		this.value = value;
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "XsdDecimal [value=" + value + "]";
	}

	@Override
	public QName getQName() {
		return qName;
	}

	@Override
	public void setQName(QName q) {
		this.qName = q;
	}

}
