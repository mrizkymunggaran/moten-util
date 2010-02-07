package moten.david.util.expression;

public class Not implements BooleanExpression{

	private final BooleanExpression a;

	public Not(BooleanExpression a) {
		this.a = a;
	}
	
	@Override
	public boolean evaluate() {
		return !a.evaluate(); 
	}

}
