package moten.david.util.expression;

import java.math.BigDecimal;

/**
 * Minus operation on numeric expressions. Marked as Infix operation.
 * 
 * @author dxm
 * 
 */
public class Minus implements NumericExpression, InfixOperation {

    private final NumericExpression a;
    private final NumericExpression b;

    /**
     * a - b
     * 
     * @param a
     * @param b
     */
    public Minus(NumericExpression a, NumericExpression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public BigDecimal evaluate() {
        return a.evaluate().add(b.evaluate().negate());
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { a, b };
    }
}
