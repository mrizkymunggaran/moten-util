package moten.david.lang.fortran.parser;

public class Do extends Node {

    private final Variable variable;
    private final Expression from;
    private final Expression to;
    private final Expression increment;
    private final Integer toLabel;

    public Do(Node parent, Integer toLabel, Variable variable, Expression from,
            Expression to, Expression increment) {
        super(parent, "do");
        this.toLabel = toLabel;
        this.variable = variable;
        this.from = from;
        this.to = to;
        this.increment = increment;
    }

    @Override
    public String toString() {
        return "do " + (toLabel == null ? "" : toLabel + " ") + variable + "="
                + from + "," + to + (increment == null ? "" : "," + increment);
    }
}
