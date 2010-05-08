package moten.david.util.math.algebra;

public class Rule {
	public Expression getReplace() {
		return replace;
	}

	public void setReplace(Expression replace) {
		this.replace = replace;
	}

	public Expression getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(Expression replaceWith) {
		this.replaceWith = replaceWith;
	}

	public Rule(Expression replace, Expression replaceWith) {
		super();
		this.replace = replace;
		this.replaceWith = replaceWith;
	}

	private Expression replace;
	private Expression replaceWith;
}
