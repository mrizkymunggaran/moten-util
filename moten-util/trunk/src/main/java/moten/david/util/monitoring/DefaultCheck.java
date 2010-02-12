package moten.david.util.monitoring;

import java.util.Set;

import moten.david.util.expression.BooleanExpression;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;

import com.google.inject.Provider;

public class DefaultCheck implements Check {
	private final String name;
	private final String description;
	private final BooleanExpression expression;
	private final Level failureLevel;
	private final Set<Check> dependencies;
	private final Set<Policy> failurePolicies;
	private final Provider<Lookup> monitoringLookup;
	private final Provider<Lookup> configurationLookup;

	public DefaultCheck(String name, String description,
			BooleanExpression expression, Provider<Lookup> monitoringLookup,
			Provider<Lookup> configurationLookup, Level failureLevel,
			Set<Check> dependencies, Set<Policy> failurePolicies) {
		super();
		this.name = name;
		this.description = description;
		this.expression = expression;
		this.monitoringLookup = monitoringLookup;
		this.configurationLookup = configurationLookup;
		this.failureLevel = failureLevel;
		this.dependencies = dependencies;
		this.failurePolicies = failurePolicies;
	}

	public DefaultCheck(String name, BooleanExpression expression,
			Level failureLevel, Set<Check> dependencies,
			Set<Policy> failurePolicies) {
		this(name, null, expression, null, null, failureLevel, dependencies,
				failurePolicies);
	}

	public Provider<Lookup> getLookup(LookupType type) {
		if (LookupType.MONITORING.equals(type))
			return monitoringLookup;
		else
			return configurationLookup;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public BooleanExpression getExpression() {
		return expression;
	}

	public Level getFailureLevel() {
		return failureLevel;
	}

	public Set<Check> getDependencies() {
		return dependencies;
	}

	public Set<Policy> getFailurePolicies() {
		return failurePolicies;
	}
}
