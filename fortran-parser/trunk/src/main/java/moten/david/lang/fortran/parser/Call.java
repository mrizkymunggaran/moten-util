package moten.david.lang.fortran.parser;

public class Call extends Node {

    private final Expression[] expressions;

    public Expression[] getExpressions() {
        return expressions;
    }

    public Call(Node parent, String name, Expression... expressions) {
        super(parent, name);
        this.expressions = expressions;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Expression e : expressions) {
            if (s.length() > 0)
                s.append(",");
            s.append(e.toString());
        }
        return "call " + getName() + "(" + s + ")";
    }

}
