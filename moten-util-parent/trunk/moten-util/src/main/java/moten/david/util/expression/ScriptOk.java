package moten.david.util.expression;

import java.util.logging.Logger;

import moten.david.util.shell.LineListenerRecorder;
import moten.david.util.shell.Shell;

public class ScriptOk implements BooleanExpression, Operation {

    private static Logger log = Logger.getLogger(ScriptOk.class.getName());

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
        LineListenerRecorder recorder = new LineListenerRecorder();
        int resultCode = shell.launch(".", expression.evaluate(), recorder);
        if (recorder.toString().length() > 0) {
            log.info("script output: ");
            log.info("-----------------------");
            String[] items = recorder.toString().split("\\n");
            for (String item : items)
                log.info(item);
            log.info("-----------------------");
        }
        return resultCode == 0;
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { expression };
    }

}
