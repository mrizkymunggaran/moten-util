package moten.david.util.math.algebra;

public class FunctionName {
	private final String name;
	private final boolean infix;
	private final boolean requiresBrackets;
	private final boolean commutative;

	public FunctionName(String name, boolean infix, boolean requiresBrackets,
			boolean commutative) {
		this.name = name;
		this.infix = infix;
		this.requiresBrackets = requiresBrackets;
		this.commutative = commutative;
	}

	public FunctionName(String name) {
		this(name, false, false, false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		FunctionName other = (FunctionName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public boolean isInfix() {
		return infix;
	}

	public boolean requiresBrackets() {
		return requiresBrackets;
	}

	public boolean isCommutative() {
		return commutative;
	}
}
