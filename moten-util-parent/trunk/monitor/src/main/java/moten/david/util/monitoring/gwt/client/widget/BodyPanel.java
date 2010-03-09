package moten.david.util.monitoring.gwt.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moten.david.util.monitoring.gwt.client.check.AppCheck;
import moten.david.util.monitoring.gwt.client.check.AppCheckResult;

public class BodyPanel extends VerticalFlowPanel {

    public BodyPanel() {
        setStyleName("bodyPanel");
        for (int i = 0; i < 5; i++) {
            add(new CheckPanel(
                    createAppCheck("container available",
                            "UrlAvailable(container.url)", "SEVERE",
                            list("NOTIFY_ON_CALL"), Collections.EMPTY_LIST,
                            list("NOTIFY_ON_CALL", "NOTIFY_DEVELOPERS"),
                            list("LOG")), createAppCheckResult("SEVERE")));
        }
    }

    private AppCheck createAppCheck(String name, String expression,
            String level, List<String> failurePolicies,
            List<String> unknownPolicies, List<String> exceptionPolicies,
            List<String> okPolicies) {
        AppCheck c = new AppCheck();
        c.setName(name);
        c.setExpression(expression);
        c.setFailureLevel(level);
        c.setFailurePolicies(failurePolicies);
        c.setExceptionPolicies(exceptionPolicies);
        c.setUnknownPolicies(unknownPolicies);
        c.setOkPolicies(okPolicies);
        return c;
    }

    public AppCheckResult createAppCheckResult(String level) {
        AppCheckResult result = new AppCheckResult();
        result.setLevel(level);
        result.setInherited(true);
        return result;
    }

    public <T> List<T> list(T... elements) {
        List<T> list = new ArrayList<T>();
        for (T t : elements)
            list.add(t);
        return list;
    }
}
