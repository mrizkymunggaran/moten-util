package moten.david.util.ete.memory;

import moten.david.ete.FixAdder;
import moten.david.ete.FixAdderImpl;
import moten.david.ete.Engine;
import moten.david.ete.memory.MyEngine;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class InjectorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Engine.class).to(MyEngine.class).in(Scopes.SINGLETON);
        bind(FixAdder.class).to(FixAdderImpl.class).in(
                Scopes.SINGLETON);
    }

}
