package moten.david.util.expression;

public class Gte implements BooleanExpression, Comparison {

	private final NumericExpression a;
	private final NumericExpression b;

	public Gte(NumericExpression a, NumericExpression b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean evaluate() {
		return a.evaluate().compareTo(b.evaluate()) >= 0;
	}

	@Override
	public Expression[] getExpressions() {
		return new Expression[] { a, b };
	}

}
