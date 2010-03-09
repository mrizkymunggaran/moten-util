package moten.david.util.monitoring.gwt.client.check;

import java.util.List;
import java.util.Set;

public class AppCheck {
    private String name;
    private String expression;
    private String failureLevel;
    private Set<AppDependency> dependencies;
    private List<String> failurePolicies;
    private List<String> unknownPolicies;
    private List<String> okPolicies;
    private List<String> exceptionPolicies;

    public List<String> getExceptionPolicies() {
        return exceptionPolicies;
    }

    public void setExceptionPolicies(List<String> exceptionPolicies) {
        this.exceptionPolicies = exceptionPolicies;
    }

    public List<String> getFailurePolicies() {
        return failurePolicies;
    }

    public void setFailurePolicies(List<String> failurePolicies) {
        this.failurePolicies = failurePolicies;
    }

    public List<String> getUnknownPolicies() {
        return unknownPolicies;
    }

    public void setUnknownPolicies(List<String> unknownPolicies) {
        this.unknownPolicies = unknownPolicies;
    }

    public List<String> getOkPolicies() {
        return okPolicies;
    }

    public void setOkPolicies(List<String> okPolicies) {
        this.okPolicies = okPolicies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setFailureLevel(String failureLevel) {
        this.failureLevel = failureLevel;
    }

    public void setDependencies(Set<AppDependency> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Briefly describes the check
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * return true if check passes
     * 
     * @return
     */
    public String getExpression() {
        return expression;
    }

    /**
     * returns the failure level if check fails
     * 
     * @return
     */
    public String getFailureLevel() {
        return failureLevel;
    }

    /**
     * This check returns unknown if any dependency does not return OK
     * 
     * @return
     */
    public Set<AppDependency> getDependencies() {
        return dependencies;
    }

}
