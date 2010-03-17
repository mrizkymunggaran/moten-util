package moten.david.util.expression;

import moten.david.util.shell.Shell;

import com.google.inject.Provider;

public class ScriptOk implements BooleanExpression, Provided<String> {

    private final Provider<String> scriptProvider;

    public ScriptOk(Provider<String> scriptProvider) {
        this.scriptProvider = scriptProvider;
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
