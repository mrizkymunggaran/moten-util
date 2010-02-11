package moten.david.util.monitoring;

import java.util.Set;

import moten.david.util.expression.BooleanExpression;

public class DefaultCheck implements Check {
	private final String name;
	private final String description;
	private final BooleanExpression expression;
	private final Level failureLevel;
	private final Set<Check> dependencies;
	private final Set<Policy> failurePolicies;

	public DefaultCheck(String name, String description,
			BooleanExpression expression, Level failureLevel,
			Set<Check> dependencies, Set<Policy> failurePolicies) {
		super();
		this.name = name;
		this.description = description;
		this.expression = expression;
		this.failureLevel = failureLevel;
		this.dependencies = dependencies;
		this.failurePolicies = failurePolicies;
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
