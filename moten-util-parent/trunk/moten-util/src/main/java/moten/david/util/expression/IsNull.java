package moten.david.util.expression;

import com.google.inject.Provider;

/**
 * Tests for null values.
 * 
 * @author dxm
 * 
 */
public class IsNull implements BooleanExpression, Provided {

    private final Provider<?> provider;

    /**
     * Any provider is fine.
     * 
     * @param provider
     */
    public IsNull(Provider<?> provider) {
        this.provider = provider;
    }

    @Override
    public boolean evaluate() {
        return provider.get() == null;
    }

    @Override
    public Provider<?> getProvider() {
        return provider;
    }

}
