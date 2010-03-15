package moten.david.util.expression;

import moten.david.util.guice.ConstantProvider;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.SingleKeyLookup;

import com.google.inject.Provider;

public class ExpressionPresenterSingleLine implements ExpressionPresenter {

    public String infix(InfixOperation infix, String symbol) {
        return infix(infix.getExpressions(), symbol);
    }

    private String infix(Expression[] expressions, String symbol) {
        StringBuffer s = new StringBuffer();
        for (Expression e : expressions) {
            if (s.length() > 0)
                s.append(" " + symbol + " ");
            s.append(string(e));
        }
        return bracket(s.toString());
    }

    private String bracket(String string) {
        return "(" + string + ")";
    }

    protected String string(Expression e) {
        if (e instanceof InfixOperation)
            return infix((InfixOperation) e, getSymbol(e));
        else if (e instanceof Comparison)
            return comparison((Comparison) e, getSymbol(e));
        else if (e instanceof Operation)
            return prefix((Operation) e, e.getClass().getSimpleName());
        else if (e instanceof Provided<?>) {
            Provider<?> provider = ((Provided<?>) e).getProvider();
            if (provider instanceof ConstantProvider<?>) {
                Object value = ((ConstantProvider<?>) provider).get();
                return value.toString();
            } else if (provider instanceof SingleKeyLookup<?>) {
                SingleKeyLookup<?> singleKeyLookup = (SingleKeyLookup<?>) provider;
                return named(singleKeyLookup.getLookupType(), singleKeyLookup
                        .getKey());
            } else
                throw new RuntimeException("unknown provider type");
        } else
            throw new RuntimeException("unknown expression type "
                    + e.getClass().getName());
    }

    private String comparison(Comparison e, String symbol) {
        return infix(e.getExpressions(), symbol);
    }

    @Override
    public String toString(Expression e) {
        String s = string(e);
        if (s.startsWith("("))
            // remove leading and trailing bracket
            return s.substring(1, s.length() - 1);
        else
            return s;
    }

    private String getSymbol(Expression e) {
        if (e instanceof And)
            return "and";
        else if (e instanceof Eq)
            return "=";
        else if (e instanceof Gt)
            return ">";
        else if (e instanceof Gte)
            return ">=";
        else if (e instanceof Lt)
            return "<";
        else if (e instanceof Lte)
            return "<=";
        else if (e instanceof Not)
            return "not";
        else if (e instanceof Neq)
            return "<>";
        else if (e instanceof Or)
            return "or";
        else if (e instanceof Plus)
            return "+";
        else if (e instanceof Minus)
            return "-";
        else if (e instanceof Times)
            return "*";
        else if (e instanceof Divide)
            return "/";
        else
            throw new RuntimeException("unknown expression type" + e);
    }

    private String named(LookupType lookupType, String name) {
        return lookupType + "." + name;
    }

    private String named(String name) {
        return name;
    }

    private String prefix(Operation op, String name) {
        StringBuffer s = new StringBuffer();
        for (Expression part : op.getExpressions()) {
            if (s.length() > 0)
                s.append(",");
            s.append(string(part));
        }
        return name + bracket(s.toString());
    }
}
