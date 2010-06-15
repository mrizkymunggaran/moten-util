package moten.david.lang.fortran.parser;

public class BooleanExpression {
    @Override
    public String toString() {
        return name;
    }

    private final String name;

    public BooleanExpression(String name) {
        this.name = name;
    }
}
