package moten.david.util.math.algebra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Util {

	private static Logger log = Logger.getLogger(Util.class.getName());

	public static Expression replaceAll(Expression expression,
			Expression expressionToReplace, Expression replaceWith) {
		Expression previousResult = null;
		Expression result = replace(expression, expressionToReplace,
				replaceWith);
		while (!result.equals(previousResult)) {
			previousResult = result;
			result = replace(result, expressionToReplace, replaceWith);
		}
		return result;
	}

	public static Expression replace(Expression expression,
			Expression expressionToReplace, Expression replaceWith) {

		Map<Marker, Expression> matches;
		if (expressionToReplace instanceof Marker)
			matches = null;
		else
			matches = matches(expression, expressionToReplace);

		if (matches != null) {
			Expression r = replaceWith;
			for (Marker marker : matches.keySet())
				r = replace(r, marker, matches.get(marker));
			return r;
		} else if (expression.equals(expressionToReplace))
			return replaceWith;
		else {
			if (expression instanceof Variable)
				return expression;
			else if (expression instanceof Function) {
				Function function = (Function) expression;
				List<Expression> params = function.parameters();
				List<Expression> list = new ArrayList<Expression>();
				for (Expression e : params)
					list.add(replace(e, expressionToReplace, replaceWith));
				return new Function(function.getFunctionName(), list
						.toArray(new Expression[] {}));
			} else if (expression instanceof Marker)
				return expression;
			else
				throw new RuntimeException("replace not implemented for: "
						+ expression);
		}
	}

	public static Map<Marker, Expression> matches(Expression e,
			Expression marked, Map<Marker, Expression> assignments) {

		if (marked instanceof Marker) {
			Expression match = assignments.get(marked);
			if (match == null) {
				Builder<Marker, Expression> builder = ImmutableMap.builder();
				builder.putAll(assignments);
				builder.put((Marker) marked, e);
				return builder.build();
			} else if (match.equals(e))
				return assignments;
			else
				return null;
		} else if (marked instanceof Variable) {
			if (e.equals(marked))
				return assignments;
			else
				return null;
		} else if (marked instanceof Function && e instanceof Function) {
			Function f1 = (Function) e;
			Function f2 = (Function) marked;
			if (!f1.name().equals(f2.name())
					|| f1.parameters().size() != f2.parameters().size())
				return null;
			Map<Marker, Expression> map = assignments;
			for (int i = 0; i < f1.parameters().size(); i++) {
				if (map != null) {
					Expression p1 = f1.parameters().get(i);
					Expression p2 = f2.parameters().get(i);
					map = matches(p1, p2, map);
				}
			}
			return map;
		} else
			return null;
	}

	public static Expression sort(Expression e) {
		return null;
	}

	public static Map<Marker, Expression> matches(Expression e,
			Expression marked) {
		Map<Marker, Expression> empty = ImmutableMap.of();
		return matches(e, marked, empty);
	}
}
