package moten.david.util.monitoring;

import java.util.Map;
import java.util.Set;

import moten.david.util.expression.BooleanExpression;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;

/**
 * An item whose status is to be checked
 * 
 * @author dave
 * 
 */
public interface Check {
    /**
     * Briefly describes the check
     * 
     * @return
     */
    String getName();

    /**
     * return true if check passes
     * 
     * @return
     */
    BooleanExpression getExpression();

    /**
     * returns the failure level if check fails
     * 
     * @return
     */
    Level getFailureLevel();

    /**
     * This check returns unknown if any dependency does not return OK
     * 
     * @return
     */
    Set<Dependency> getDependencies();

    /**
     * Provides lookup for the check
     * 
     * @return
     */
    Map<LookupType, Lookup> getLookups();

}
