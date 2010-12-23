package moten.david.matchstack.datastore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import moten.david.matchstack.datastore.DatastoreImmutable;
import moten.david.matchstack.datastore.DatastoreImmutableFactory;
import moten.david.matchstack.types.IdentifierTypeStrengthComparator;
import moten.david.matchstack.types.IdentifierTypeStrictComparator;
import moten.david.matchstack.types.impl.MyIdentifierTypeStrengthComparator;
import moten.david.matchstack.types.impl.MyIdentifierTypeStrictComparator;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryProvider;

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
		bind(ExecutorService.class).toInstance(Executors.newFixedThreadPool(8));
		// bindInterceptor(Matchers.subclassesOf(DatastoreImmutable.class),
		// Matchers.any(), Profiler.getInstance());

	}

}
