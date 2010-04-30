package moten.david.imatch.memory;

import moten.david.imatch.IdentifierTypeStrengthComparator;
import moten.david.imatch.IdentifierTypeStrictComparator;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.matcher.Matchers;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IdentifierTypeStrengthComparator.class).to(
				MyIdentifierTypeStrengthComparator.class).in(Scopes.SINGLETON);
		bind(IdentifierTypeStrictComparator.class).to(
				MyIdentifierTypeStrictComparator.class).in(Scopes.SINGLETON);
		bind(DatastoreImmutableFactory.class).toProvider(
				FactoryProvider.newFactory(DatastoreImmutableFactory.class,
						DatastoreImmutable.class)).in(Scopes.SINGLETON);
		bindInterceptor(Matchers.subclassesOf(DatastoreImmutable.class),
				Matchers.any(), Profiler.getInstance());

	}

}
