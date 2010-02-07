package moten.david.util.expression;

public class And implements BooleanExpression{
	private final BooleanExpression a;
	private final BooleanExpression b;

	public And(BooleanExpression a, BooleanExpression b ){
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean evaluate() {
		return a.evaluate() && b.evaluate(); 
	}
	
}
