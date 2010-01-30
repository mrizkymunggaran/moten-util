package moten.david.util.tv.updater;

import moten.david.util.tv.ChannelsProvider;
import moten.david.util.tv.Configuration;
import moten.david.util.tv.ozlist.ChannelsProviderOzList;
import moten.david.util.tv.ozlist.ProgrammeProviderOzList;
import moten.david.util.tv.programme.ProgrammeProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ApplicationInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Configuration.class).in(Scopes.SINGLETON);
		bind(ChannelsProvider.class).to(ChannelsProviderOzList.class).in(
				Scopes.SINGLETON);
		bind(ProgrammeProvider.class).to(ProgrammeProviderOzList.class).in(
				Scopes.SINGLETON);
	}

}
