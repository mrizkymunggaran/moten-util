package moten.david.imatch.memory;

import moten.david.imatch.IdentifierSetFactory;
import moten.david.imatch.IdentifierTypeSetFactory;
import moten.david.imatch.IdentifierTypeStrengthComparator;
import moten.david.imatch.IdentifierTypeStrictComparator;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryProvider;

public class InjectorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IdentifierSetFactory.class).to(MyIdentifierSetFactory.class);
        bind(IdentifierTypeSetFactory.class).to(
                MyIdentifierTypeSetFactory.class);
        bind(IdentifierTypeStrengthComparator.class).to(
                MyIdentifierTypeStrengthComparator.class).in(Scopes.SINGLETON);
        bind(IdentifierTypeStrictComparator.class).to(
                MyIdentifierTypeStrictComparator.class).in(Scopes.SINGLETON);
        bind(DatastoreImmutableFactory.class).toProvider(
                FactoryProvider.newFactory(DatastoreImmutableFactory.class,
                        DatastoreImmutable.class)).in(Scopes.SINGLETON);

    }
}
