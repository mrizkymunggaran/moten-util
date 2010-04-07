package moten.david.imatch.memory;

import moten.david.imatch.IdentifierSetFactory;
import moten.david.imatch.IdentifierTypeSetFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

public class InjectorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IdentifierSetFactory.class).to(MyIdentifierSetFactory.class);
        bind(IdentifierTypeSetFactory.class).to(
                MyIdentifierTypeSetFactory.class);
        bind(DatastoreImmutableFactory.class).toProvider(
                FactoryProvider.newFactory(DatastoreImmutableFactory.class,
                        DatastoreImmutable.class));

    }

}
