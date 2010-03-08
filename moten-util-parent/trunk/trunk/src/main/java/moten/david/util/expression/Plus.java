package moten.david.util.expression;

import java.math.BigDecimal;

public class Plus implements NumericExpression, InfixOperation {

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

	@Override
	public Expression[] getExpressions() {
		return new Expression[] { a, b };
	}

}
