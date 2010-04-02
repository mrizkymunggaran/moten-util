package moten.david.util.ete.memory;

import moten.david.ete.Engine;
import moten.david.ete.FixAdder;
import moten.david.ete.FixAdderImpl;
import moten.david.ete.memory.EntityLookup;
import moten.david.ete.memory.EntityLookupMap;
import moten.david.ete.memory.MyEngine;
import moten.david.ete.memory.MyEntity;
import moten.david.ete.memory.MyEntityFactory;
import moten.david.ete.memory.MyIdentifiers;
import moten.david.ete.memory.MyIdentifiersFactory;
import moten.david.util.controller.Controller;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryProvider;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Engine.class).to(MyEngine.class).in(Scopes.SINGLETON);
		bind(FixAdder.class).to(FixAdderImpl.class).in(Scopes.SINGLETON);
		bind(MyIdentifiersFactory.class).toProvider(
				FactoryProvider.newFactory(MyIdentifiersFactory.class,
						MyIdentifiers.class));
		bind(MyEntityFactory.class).toProvider(
				FactoryProvider.newFactory(MyEntityFactory.class,
						MyEntity.class));
		bind(Controller.class).in(Scopes.SINGLETON);
		bind(EntityLookup.class).to(EntityLookupMap.class);
	}

}
