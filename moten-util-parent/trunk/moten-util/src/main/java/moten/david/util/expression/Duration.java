package moten.david.util.expression;

import java.math.BigDecimal;

import moten.david.util.guice.ConstantProvider;

import com.google.inject.Provider;

/**
 * Duration stored in milliseconds.
 * 
 * @author dxm
 * 
 */
public class Duration extends Numeric {

    /**
     * Duration as a provided value with a unit (type).
     * 
     * @param provider
     * @param type
     */
    public Duration(final Provider<BigDecimal> provider, final DurationType type) {
        super(new Provider<BigDecimal>() {
            @Override
            public BigDecimal get() {
                return provider.get()
                        .multiply(new BigDecimal(type.getFactor()));
            }
        });
    }

    /**
     * Duration as a value with a unit (type).
     * 
     * @param provider
     * @param type
     */
    public Duration(BigDecimal value, DurationType type) {
        this(new ConstantProvider<BigDecimal>(value), type);
    }
}
