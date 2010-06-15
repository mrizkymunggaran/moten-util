package moten.david.lang.fortran.parser;

public class Assignment extends Node {

    private final Expression expression;
    private final Variable variable;

    public Assignment(Node parent, Variable variable, Expression expression,
            Expression... indexes) {
        super(parent, variable.toString() + ":=" + expression);
        this.variable = variable;
        this.expression = expression;
    }

}
