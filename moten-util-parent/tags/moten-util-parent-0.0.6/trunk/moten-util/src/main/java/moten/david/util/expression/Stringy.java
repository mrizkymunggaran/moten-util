package moten.david.util.expression;

import moten.david.util.guice.ConstantProvider;

import com.google.inject.Provider;

public class Stringy implements StringExpression, Provided<String> {

    private final Provider<String> provider;

    public Stringy(Provider<String> provider) {
        this.provider = provider;
    }

    public Stringy(String s) {
        this(new ConstantProvider<String>(s));
    }

    @Override
    public String evaluate() {
        return provider.get();
    }

    @Override
    public Provider<String> getProvider() {
        return provider;
    }

}
