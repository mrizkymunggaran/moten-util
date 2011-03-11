package org.moten.david.util.xsd.simplified;

import java.math.BigDecimal;

import javax.xml.namespace.QName;

public class XsdDecimal implements XsdType<BigDecimal> {

	private final BigDecimal value;

	public XsdDecimal(BigDecimal value) {
		super();
		this.value = value;
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}

	private static final QName qName = new QName(Schema.XML_SCHEMA_NAMESPACE,
			"decimal");

	@Override
	public QName getQName() {
		return qName;
	}

	@Override
	public String toString() {
		return "XsdDecimal [value=" + value + "]";
	}

}