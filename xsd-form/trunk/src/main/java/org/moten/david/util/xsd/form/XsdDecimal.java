package org.moten.david.util.xsd.form;

import java.math.BigDecimal;

public class XsdDecimal implements XsdType<BigDecimal> {

	private BigDecimal value;

	public BigDecimal getValue() {
		return value;
	}

}
