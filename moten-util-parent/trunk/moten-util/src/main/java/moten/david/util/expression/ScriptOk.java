package moten.david.util.expression;

import moten.david.util.guice.ConstantProvider;
import moten.david.util.shell.Shell;

import com.google.inject.Provider;

public class ScriptOk implements BooleanExpression, Provided<String> {

    private final Provider<String> scriptProvider;

    public ScriptOk(Provider<String> scriptProvider) {
        this.scriptProvider = scriptProvider;
    }

    public ScriptOk(String script) {
        this(new ConstantProvider<String>(script));
    }

    @Override
    public boolean evaluate() {
        Shell shell = new Shell();
        int resultCode = shell.launch(".", scriptProvider.get());
        return resultCode == 0;
    }

    @Override
    public Provider<String> getProvider() {
        return scriptProvider;
    }

}
