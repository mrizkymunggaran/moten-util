package moten.david.util.expression;

import java.math.BigDecimal;

public class Divide implements NumericExpression {

	private final NumericExpression a;
	private final NumericExpression b;

	public Divide(NumericExpression a, NumericExpression b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public BigDecimal evaluate() {
		return a.evaluate().divide(b.evaluate());
	}

}
