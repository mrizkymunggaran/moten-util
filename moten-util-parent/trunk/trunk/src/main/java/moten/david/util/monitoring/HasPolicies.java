package moten.david.util.monitoring;

import java.util.Set;

public interface HasPolicies {
    /**
     * On failure policies are mapped (presumably) to actions by a monitoring
     * system
     * 
     * @return
     */
    Set<Policy> getFailurePolicies();

    /**
     * On exception during check these policies would be mapped to an action by
     * a monitoring system.
     * 
     * @return
     */
    Set<Policy> getExceptionPolicies();

    /**
     * On unknown status these policies would be mapped to an action by a
     * monitoring system.
     * 
     * @return
     */
    Set<Policy> getUnknownPolicies();

    /**
     * On ok status these policies would be mapped to an action by a monitoring
     * system (might just be a log action).
     * 
     * @return
     */
    Set<Policy> getOkPolicies();
}
