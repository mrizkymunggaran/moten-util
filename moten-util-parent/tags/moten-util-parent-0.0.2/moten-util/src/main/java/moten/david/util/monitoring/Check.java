package moten.david.util.monitoring;

import java.util.Set;

import moten.david.util.expression.BooleanExpression;
import moten.david.util.monitoring.lookup.LookupParameters;

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
	 * Returns the evaluation context for the check.
	 * 
	 * @return
	 */
	EvaluationContext getEvaluationContext();

	/**
	 * Provides parameter for each lookup.
	 * 
	 * @return
	 */
	LookupParameters getParameters();

}
