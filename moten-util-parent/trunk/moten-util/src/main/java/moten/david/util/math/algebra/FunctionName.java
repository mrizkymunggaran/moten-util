package moten.david.util.math.algebra;

public class FunctionName {
	private final String name;
	private final boolean infix;
	private final boolean requiresBrackets;

	public FunctionName(String name, boolean infix, boolean requiresBrackets) {
		this.name = name;
		this.infix = infix;
		this.requiresBrackets = requiresBrackets;
	}

	public FunctionName(String name) {
		this(name, false, false);
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
}
