package moten.david.lang.fortran.parser;

public class If extends Node {

    private final BooleanExpression exp;

    public If(Node parent, BooleanExpression exp) {
        super(parent, "if");
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "if (" + exp + ") then";
    }

}
