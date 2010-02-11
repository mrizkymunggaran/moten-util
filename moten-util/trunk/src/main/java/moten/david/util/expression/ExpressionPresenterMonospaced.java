package moten.david.util.expression;

import moten.david.util.guice.ConstantProvider;
import moten.david.util.monitoring.lookup.SingleKeyLookup;

import com.google.inject.Provider;

public class ExpressionPresenterMonospaced implements ExpressionPresenter {

	public String infix(InfixOperation infix, String symbol) {
		StringBuffer s = new StringBuffer();
		for (Expression e : infix.getExpressions()) {
			if (s.length() > 0)
				s.append(" " + symbol + " ");
			s.append(string(e));
		}
		return bracket(s.toString());
	}

	private String bracket(String string) {
		return "(" + string + ")";
	}

	protected String string(Expression e) {
		if (e instanceof InfixOperation)
			return infix((InfixOperation) e, getSymbol(e));
		else if (e instanceof Operation)
			return prefix(e, e.getClass().getSimpleName());
		else if (e instanceof Provided<?>) {
			Provider<?> provider = ((Provided<?>) e).getProvider();
			if (provider instanceof ConstantProvider<?>) {
				Object value = ((ConstantProvider<?>) provider).get();
				return value.toString();
			} else if (provider instanceof SingleKeyLookup<?>) {
				return named(((SingleKeyLookup<?>) provider).getKey());
			} else
				throw new RuntimeException("unknown provider type");
		} else
			throw new RuntimeException("unknown expression type");
	}

	@Override
	public String toString(Expression e) {
		String s = string(e);
		if (s.startsWith("("))
			// remove leading and trailing bracket
			return s.substring(1, s.length() - 1);
		else
			return s;
	}

	private String getSymbol(Expression e) {
		if (e instanceof And)
			return "and";
		else if (e instanceof Eq)
			return "=";
		else if (e instanceof Gt)
			return ">";
		else if (e instanceof Gte)
			return ">=";
		else if (e instanceof Lt)
			return "<";
		else if (e instanceof Lte)
			return "<=";
		else if (e instanceof Not)
			return "not";
		else if (e instanceof Neq)
			return "<>";
		else if (e instanceof Or)
			return "or";
		else if (e instanceof Plus)
			return "+";
		else if (e instanceof Minus)
			return "-";
		else if (e instanceof Times)
			return "*";
		else if (e instanceof Divide)
			return "/";
		else
			throw new RuntimeException("unknown expression type" + e);
	}

	private String named(String name) {
		return name;
	}

	private String prefix(Expression e, String name) {
		return name + bracket(string(e));
	}
}
