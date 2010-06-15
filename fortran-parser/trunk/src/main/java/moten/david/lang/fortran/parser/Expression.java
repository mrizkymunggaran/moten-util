package moten.david.lang.fortran.parser;

public class Expression {
    private final String expression;

    public Expression(String expression) {
        this.expression = expression;

    }

    @Override
    public String toString() {
        return expression;
    }
}
