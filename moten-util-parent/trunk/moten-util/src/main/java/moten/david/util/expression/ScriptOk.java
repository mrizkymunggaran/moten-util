package moten.david.util.expression;

import moten.david.util.shell.Shell;

public class ScriptOk implements BooleanExpression, Operation {

    private final StringExpression expression;

    public ScriptOk(StringExpression expression) {
        this.expression = expression;
    }

    public ScriptOk(String script) {
        this(new Stringy(script));
    }

    @Override
    public boolean evaluate() {
        Shell shell = new Shell();
        int resultCode = shell.launch(".", expression.evaluate());
        return resultCode == 0;
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { expression };
    }

}
