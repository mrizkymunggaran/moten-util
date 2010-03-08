package moten.david.util.expression;

import java.math.BigDecimal;

public interface NumericExpression extends Expression{
	BigDecimal evaluate();
}
