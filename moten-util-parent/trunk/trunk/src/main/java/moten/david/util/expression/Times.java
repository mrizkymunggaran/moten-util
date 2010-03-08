package moten.david.util.expression;

import java.math.BigDecimal;

public class Times implements NumericExpression, InfixOperation {

	private final NumericExpression a;
	private final NumericExpression b;

	public Times(NumericExpression a, NumericExpression b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public BigDecimal evaluate() {
		return a.evaluate().multiply(b.evaluate());
	}

	@Override
	public Expression[] getExpressions() {
		return new Expression[] { a, b };
	}
}
