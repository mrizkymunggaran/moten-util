package moten.david.util.monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import moten.david.util.expression.BooleanExpression;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;

public class DefaultCheck implements Check {
	private final String name;
	private final String description;
	private final BooleanExpression expression;
	private final Level failureLevel;
	private final Set<Check> dependencies;
	private final Set<Policy> failurePolicies;
	private final Map<LookupType, Lookup> lookups;
	private final LookupType lookupTypeDefault;

	public DefaultCheck(String name, String description,
			BooleanExpression expression, Map<LookupType, Lookup> lookups,
			LookupType lookupTypeDefault, Level failureLevel,
			Set<Check> dependencies, Set<Policy> failurePolicies) {
		super();
		this.name = name;
		this.description = description;
		this.expression = expression;
		this.lookups = lookups;
		this.lookupTypeDefault = lookupTypeDefault;
		this.failureLevel = failureLevel;
		this.dependencies = dependencies;
		this.failurePolicies = failurePolicies;
	}

	public DefaultCheck(String name, BooleanExpression expression,
			Level failureLevel, Set<Check> dependencies,
			Set<Policy> failurePolicies) {
		this(name, null, expression, new HashMap<LookupType, Lookup>(), null,
				failureLevel, dependencies, failurePolicies);
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

	@Override
	public Map<LookupType, Lookup> getLookups() {
		return lookups;
	}

	@Override
	public LookupType getLookupTypeDefault() {
		return lookupTypeDefault;
	}
}
