package moten.david.util.expression;

/**
 * Logical Not
 * 
 * @author dxm
 * 
 */
public class Not implements BooleanExpression, Operation {

    private final BooleanExpression a;

    public Not(BooleanExpression a) {
        this.a = a;
    }

    @Override
    public boolean evaluate() {
        return !a.evaluate();
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { a };
    }
}
