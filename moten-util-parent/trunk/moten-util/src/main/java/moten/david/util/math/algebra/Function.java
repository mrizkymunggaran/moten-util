package moten.david.util.math.algebra;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Function implements Expression, Named {

	private final List<Expression> parameters;
	private final FunctionName functionName;

	public Function(FunctionName functionName, Expression... parameters) {
		this.functionName = functionName;
		Builder<Expression> builder = ImmutableList.builder();
		for (Expression parameter : parameters)
			builder.add(parameter);
		this.parameters = builder.build();
	}

	public List<Expression> parameters() {
		return parameters;
	}

	public String name() {
		return functionName.getName();
	}

	public FunctionName getFunctionName() {
		return functionName;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (functionName.isInfix()) {
			for (Expression p : parameters) {
				if (s.length() > 0)
					s.append(" " + functionName.getName() + " ");
				s.append(p.toString());
			}
			if (requiresBrackets()) {
				s.insert(0, "(");
				s.append(")");
			}
		} else {
			for (Expression p : parameters) {
				if (s.length() > 0)
					s.append(",");
				s.append(p.toString());
			}
			s.insert(0, "(");
			s.append(")");
			s.insert(0, functionName.getName());
		}
		return s.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((functionName == null) ? 0 : functionName.hashCode());
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
		if (functionName == null) {
			if (other.functionName != null)
				return false;
		} else if (!functionName.equals(other.functionName))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	public boolean requiresBrackets() {
		return functionName.requiresBrackets();
	}

	public boolean isCommutative() {
		return functionName.isCommutative();
	}

	@Override
	public int compareTo(Expression o) {
		if (o instanceof Named)
			return functionName.getName().compareTo(((Named) o).name());
		else
			return 0;
	}

}
