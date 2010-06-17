package moten.david.util.expression;

/**
 * An expression that evaluates to a boolean value (true or false).
 * 
 * @author dxm
 * 
 */
public interface BooleanExpression extends Expression {
    /**
     * Evaluate the expression
     * 
     * @return the result of the evaluation (true or false)
     */
    boolean evaluate();
}
