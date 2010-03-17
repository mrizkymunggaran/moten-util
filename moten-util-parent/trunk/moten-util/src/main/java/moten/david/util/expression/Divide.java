package moten.david.util.expression;

import java.math.BigDecimal;

/**
 * Divides two numeric expressions.
 * 
 * @author dxm
 * 
 */
public class Divide implements NumericExpression, InfixOperation {

    private final NumericExpression a;
    private final NumericExpression b;

    /**
     * a / b
     * 
     * @param a
     * @param b
     */
    public Divide(NumericExpression a, NumericExpression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public BigDecimal evaluate() {
        return a.evaluate().divide(b.evaluate());
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { a, b };
    }
}
