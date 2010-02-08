package moten.david.util.expression;

import java.math.BigDecimal;

public class Plus implements NumericExpression {

	private final NumericExpression a;
	private final NumericExpression b;

	public Plus(NumericExpression a, NumericExpression b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public BigDecimal evaluate() {
		return a.evaluate().add(b.evaluate());
	}

}
