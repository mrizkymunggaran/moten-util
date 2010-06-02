package moten.david.markup;

import moten.david.util.controller.Controller;
import moten.david.util.controller.SynchronousController;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Controller.class).to(SynchronousController.class).in(
				Scopes.SINGLETON);
		bind(CurrentStudy.class).in(Scopes.SINGLETON);
	}

}
