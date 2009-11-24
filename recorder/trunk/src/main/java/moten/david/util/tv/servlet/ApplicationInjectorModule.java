package moten.david.util.tv.servlet;

import moten.david.util.tv.ChannelsProvider;
import moten.david.util.tv.Configuration;
import moten.david.util.tv.ozlist.ChannelsProviderOzList;
import moten.david.util.tv.ozlist.ProgrammeProviderOzList;
import moten.david.util.tv.programme.ProgrammeProvider;
import moten.david.util.tv.recorder.AliasProvider;
import moten.david.util.tv.recorder.AliasProviderImpl;
import moten.david.util.tv.recorder.Recorder;
import moten.david.util.tv.recorder.RecorderLinux;
import moten.david.util.tv.schedule.Schedule;
import moten.david.util.tv.schedule.ScheduleSerialized;

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
		bind(Recorder.class).to(RecorderLinux.class).in(Scopes.SINGLETON);
		bind(AliasProvider.class).to(AliasProviderImpl.class).in(
				Scopes.SINGLETON);
		bind(Schedule.class).to(ScheduleSerialized.class).in(Scopes.SINGLETON);
	}

}
