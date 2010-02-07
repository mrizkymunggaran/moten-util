package moten.david.util.monitoring;

import java.util.List;
import java.util.Set;

import moten.david.util.expression.BooleanExpression;

public interface Check {
	String getName();

	String getDescription();

	BooleanExpression getExpression();

	Level getLevel();

	List<Policy> getPolicies();

	/**
	 * This check returns unknown if any dependency does not return OK
	 * 
	 * @return
	 */
	Set<Check> getDependencies();
}
