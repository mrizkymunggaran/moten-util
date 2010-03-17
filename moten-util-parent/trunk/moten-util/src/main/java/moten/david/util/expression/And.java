package moten.david.util.expression;

/**
 * Logical 'and' using short-circuiting like &&
 * 
 * @author dxm
 * 
 */
public class And implements BooleanExpression, InfixOperation {
    private final BooleanExpression a;
    private final BooleanExpression b;

    /**
     * a && b
     * 
     * @param a
     * @param b
     */
    public And(BooleanExpression a, BooleanExpression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean evaluate() {
        return a.evaluate() && b.evaluate();
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { a, b };
    }

}
