package moten.david.util.monitoring.gwt.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.CheckResult;
import moten.david.util.monitoring.DefaultCheck;
import moten.david.util.monitoring.Dependency;
import moten.david.util.monitoring.Policy;
import moten.david.util.monitoring.gwt.client.check.AppCheck;
import moten.david.util.monitoring.gwt.client.check.AppCheckResult;
import moten.david.util.monitoring.gwt.client.check.AppChecks;
import moten.david.util.monitoring.gwt.client.check.AppDependency;

import com.google.inject.Inject;

/**
 * Converts server side monitoring objects to gwt client side objects.
 * 
 * @author dxm
 * 
 */
public class Convertor {

    private final ExpressionPresenter presenter;

    @Inject
    public Convertor(ExpressionPresenter presenter) {
        this.presenter = presenter;
    }

    public AppChecks createAppChecks(List<Check> checks,
            Map<Check, CheckResult> results) {
        AppChecks all = new AppChecks();
        List<AppCheck> appChecks = new ArrayList<AppCheck>();
        for (Check check : checks) {
            AppCheck appCheck = createAppCheck(check, results);
            appChecks.add(appCheck);
        }
        all.setChecks(appChecks.toArray(new AppCheck[] {}));

        List<AppCheckResult> appCheckResults = new ArrayList<AppCheckResult>();

        for (Check check : results.keySet())
            appCheckResults
                    .add(createAppCheckResult(check, results.get(check)));

        all.setResults(appCheckResults.toArray(new AppCheckResult[] {}));
        return all;
    }

    private AppCheckResult createAppCheckResult(Check check,
            CheckResult checkResult) {
        AppCheckResult a = new AppCheckResult();
        a.setException(toString(checkResult.getException()));
        a.setInherited(checkResult.isInherited());
        a.setLevel(checkResult.getLevel().toString());
        a.setName(check.getName());
        return a;
    }

    private String toString(Throwable t) {
        if (t == null)
            return null;
        else {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(bytes));
            return bytes.toString();
        }
    }

    private AppCheck createAppCheck(Check check, Map<Check, CheckResult> results) {
        AppCheck a = new AppCheck();
        a.setName(check.getName());
        a.setFailureLevel(check.getFailureLevel().toString());
        a.setDependencies(createAppDependencies(check.getDependencies(),
                results));
        a.setExpression(presenter.toString(check.getExpression()));
        if (check instanceof DefaultCheck) {
            DefaultCheck d = (DefaultCheck) check;
            a.setExceptionPolicies(createPolicies(d.getExceptionPolicies()));
            a.setFailurePolicies(createPolicies(((DefaultCheck) check)
                    .getFailurePolicies()));
            a.setOkPolicies(createPolicies(d.getOkPolicies()));
            a.setUnknownPolicies(createPolicies(d.getUnknownPolicies()));
        }
        return a;
    }

    private List<String> createPolicies(Set<Policy> policies) {
        if (policies == null)
            return null;
        ArrayList<String> list = new ArrayList<String>();
        for (Policy policy : policies)
            list.add(policy.toString());
        return list;
    }

    private Set<AppDependency> createAppDependencies(
            Set<Dependency> dependencies, Map<Check, CheckResult> results) {
        if (dependencies == null)
            return null;
        Set<AppDependency> result = new HashSet<AppDependency>();
        for (Dependency dependency : dependencies)
            result.add(createAppDependency(dependency, results));
        return result;
    }

    private AppDependency createAppDependency(Dependency dependency,
            Map<Check, CheckResult> results) {
        AppDependency a = new AppDependency();
        a.setCheck(createAppCheck(dependency.getCheck(), results));
        a.setLevelInherited(dependency.isLevelInherited());
        return a;
    }

}
