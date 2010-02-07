package moten.david.util.expression;

public class Eq implements BooleanExpression {
	private NumericExpression a;
	private NumericExpression b;

	public Eq(NumericExpression a, NumericExpression b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean evaluate() {
		return a.evaluate().equals(b.evaluate());
	}
	
	
}
