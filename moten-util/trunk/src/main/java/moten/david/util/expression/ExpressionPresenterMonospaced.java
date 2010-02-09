package moten.david.util.expression;

public class ExpressionPresenterMonospaced implements ExpressionPresenter {

	public String infix(InfixOperation infix, String symbol) {
		StringBuffer s = new StringBuffer();
		for (Expression e : infix.getExpressions()) {
			if (s.length() > 0)
				s.append(symbol + toString(e));
		}
		return bracket(s.toString());
	}

	private String bracket(String string) {
		return "(" + string + ")";
	}

	@Override
	public String toString(Expression e) {
		if (e instanceof InfixOperation)
			return infix((InfixOperation) e, e.getClass().getSimpleName());
		else if (e instanceof Operation)
			return prefix(e, e.getClass().getSimpleName());
		else if (e instanceof Numeric)
			return "?";
		else
			throw new RuntimeException("unknown expression type");
	}

	private String prefix(Expression e, String name) {
		return name + bracket(toString(e));
	}
}
