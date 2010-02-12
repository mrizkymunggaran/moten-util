package moten.david.util.monitoring;

import java.util.Map;
import java.util.Set;

import moten.david.util.expression.BooleanExpression;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;

/**
 * An item to be monitored
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
	 * Describes the check
	 * 
	 * @return
	 */
	String getDescription();

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
	 * On failure policies are mapped (presumably) to actions by a monitoring
	 * system
	 * 
	 * @return
	 */
	Set<Policy> getFailurePolicies();

	/**
	 * This check returns unknown if any dependency does not return OK
	 * 
	 * @return
	 */
	Set<Check> getDependencies();

	/**
	 * Provides lookup for the
	 * 
	 * @return
	 */
	Map<LookupType, Lookup> getLookups();

	/**
	 * The default lookup type (normally corresponding to the lookup of the
	 * application status). i.e. LookupType.MONITORING
	 * 
	 * @return
	 */
	LookupType getLookupTypeDefault();

}
