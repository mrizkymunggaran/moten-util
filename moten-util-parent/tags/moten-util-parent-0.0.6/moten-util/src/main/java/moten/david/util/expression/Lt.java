package moten.david.util.expression;

/**
 * Less than comparison.
 * 
 * @author dxm
 * 
 */
public class Lt implements Comparison {

    private final NumericExpression a;
    private final NumericExpression b;

    /**
     * a < b
     * 
     * @param a
     * @param b
     */
    public Lt(NumericExpression a, NumericExpression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean evaluate() {
        return a.evaluate().compareTo(b.evaluate()) < 0;
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { a, b };
    }
}
