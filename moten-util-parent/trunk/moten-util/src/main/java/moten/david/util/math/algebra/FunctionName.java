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
