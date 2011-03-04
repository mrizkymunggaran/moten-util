package org.moten.david.util.xsd.simplified;

import java.math.BigDecimal;

import javax.xml.namespace.QName;

public class XsdDecimal implements XsdType<BigDecimal> {

	private BigDecimal value;

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

}
