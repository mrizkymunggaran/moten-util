package moten.david.util.math.algebra;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Function implements Expression, Named {

	private final List<Expression> parameters;
	private final boolean infix;

	private final String name;
	private final boolean requiresBrackets;
	private final boolean commutative;

	public Function(String name, Expression... parameters) {
		this(name, false, false, false, parameters);
	}

	public Function(FunctionName functionName, Expression... parameters) {
		this(functionName.getName(), functionName.isInfix(), functionName
				.requiresBrackets(), functionName.isCommutative(), parameters);
	}

	public Function(String name, boolean infix, boolean requiresBrackets,
			boolean commutative, Expression... parameters) {
		this.name = name;
		this.infix = infix;
		this.requiresBrackets = requiresBrackets;
		this.commutative = commutative;
		Builder<Expression> builder = ImmutableList.builder();
		for (Expression parameter : parameters)
			builder.add(parameter);
		this.parameters = builder.build();
	}

	public List<Expression> parameters() {
		return parameters;
	}

	public String name() {
		return name;
	}

	public boolean isInfix() {
		return infix;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (infix) {
			for (Expression p : parameters) {
				if (s.length() > 0)
					s.append(" " + name + " ");
				s.append(p.toString());
				if (requiresBrackets) {
					s.insert(0, "(");
					s.append(")");
				}
			}
		} else {
			for (Expression p : parameters) {
				if (s.length() > 0)
					s.append(",");
				s.append(p.toString());
			}
			s.insert(0, "(");
			s.append(")");
			s.insert(0, name);
		}
		return s.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Function other = (Function) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	public boolean requiresBrackets() {
		return requiresBrackets;
	}

	public boolean isCommutative() {
		return commutative;
	}

}
