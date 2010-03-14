package moten.david.util.monitoring;

import java.util.Collections;
import java.util.Set;

import moten.david.util.expression.BooleanExpression;
import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.monitoring.lookup.LookupParameters;

public class DefaultCheck implements Check, HasPolicies {
	private final String name;
	private final String description;
	private final BooleanExpression expression;
	private final Level failureLevel;
	private final Set<Dependency> dependencies;
	private final Set<Policy> failurePolicies;
	private final LookupParameters parameters;
	private final EvaluationContext evaluationContext;

	/**
	 * Creates a Check implementation based on the given parameters. Note that
	 * DefaultCheck stores a copy of the LookupParameters map so that changes to
	 * the parameters parameter later don't affect the check.
	 * 
	 * Exception policies are assumed to be the same as failure policies and
	 * Unknown policies is empty.
	 * 
	 * @param name
	 * @param description
	 * @param expression
	 * @param evaluationContext
	 * @param parameters
	 * @param failureLevel
	 * @param dependencies
	 * @param failurePolicies
	 */
	public DefaultCheck(String name, String description,
			BooleanExpression expression, EvaluationContext evaluationContext,
			LookupParameters parameters, Level failureLevel,
			Set<Dependency> dependencies, Set<Policy> failurePolicies) {
		super();
		this.name = name;
		this.description = description;
		this.expression = expression;
		this.evaluationContext = evaluationContext;
		this.parameters = parameters;
		this.failureLevel = failureLevel;
		this.dependencies = dependencies;
		this.failurePolicies = failurePolicies;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public BooleanExpression getExpression() {
		return expression;
	}

	@Override
	public Level getFailureLevel() {
		return failureLevel;
	}

	@Override
	public Set<Dependency> getDependencies() {
		return dependencies;
	}

	@Override
	public Set<Policy> getFailurePolicies() {
		return failurePolicies;
	}

	@Override
	public String toString() {
		return name;
	}

	public String present(ExpressionPresenter presenter) {
		StringBuffer s = new StringBuffer();
		s.append(name);
		s.append("\t");
		s.append(presenter.toString(this.expression));
		return s.toString();
	}

	@Override
	public Set<Policy> getExceptionPolicies() {
		return failurePolicies;
	}

	@Override
	public Set<Policy> getUnknownPolicies() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<Policy> getOkPolicies() {
		return Collections.EMPTY_SET;
	}

	@Override
	public LookupParameters getParameters() {
		return parameters;
	}

	@Override
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
}
