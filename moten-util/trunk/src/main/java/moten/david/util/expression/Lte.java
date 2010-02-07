package moten.david.util.expression;

public class Lte implements BooleanExpression {

	private final NumericExpression a;
	private final NumericExpression b;

	public Lte(NumericExpression a, NumericExpression b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean evaluate() {
		return a.evaluate().compareTo(b.evaluate()) <= 0;
	}

}
