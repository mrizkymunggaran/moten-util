package moten.david.util.expression;

public class Neq implements BooleanExpression {
	private final NumericExpression a;
	private final NumericExpression b;

	public Neq(NumericExpression a, NumericExpression b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean evaluate() {
		return a.evaluate().compareTo(b.evaluate()) != 0;
	}

}
