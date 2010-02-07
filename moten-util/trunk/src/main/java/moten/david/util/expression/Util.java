package moten.david.util.expression;

public class Util {

	public static BooleanExpression and(BooleanExpression a, BooleanExpression b) {
		return new And(a, b);
	}

	public static BooleanExpression or(BooleanExpression a, BooleanExpression b) {
		return new Or(a, b);
	}

	public static BooleanExpression not(BooleanExpression a) {
		return new Not(a);
	}
	
	public static BooleanExpression eq(NumericExpression a, NumericExpression b){ 
		return new Eq(a,b);
	}
}
