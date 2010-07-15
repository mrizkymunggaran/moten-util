package moten.david.ets.server;

import moten.david.matchstack.types.IdentifierTypeStrengthComparator;
import moten.david.matchstack.types.IdentifierTypeStrictComparator;
import moten.david.matchstack.types.impl.MyIdentifierTypeStrengthComparator;
import moten.david.matchstack.types.impl.MyIdentifierTypeStrictComparator;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

/**
 * Guice Injector module.
 * 
 * @author dxm
 */
public class InjectorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ObjectDatastore.class).to(AnnotationObjectDatastore.class).in(
                Scopes.SINGLETON);
        bind(Entities.class).to(EntitiesGae.class).in(Scopes.SINGLETON);
        bind(IdentifierTypeStrictComparator.class).to(
                MyIdentifierTypeStrictComparator.class).in(Scopes.SINGLETON);
        bind(IdentifierTypeStrengthComparator.class).to(
                MyIdentifierTypeStrengthComparator.class).in(Scopes.SINGLETON);

    }

}
