package moten.david.util.tv.servlet;

import java.util.HashSet;
import java.util.Set;

import moten.david.util.tv.updater.Updater;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UpdaterJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Set<String> channels = new HashSet<String>();
		channels.add("ABC2");
		channels.add("ABC-Can");
		channels.add("Prime-Can");
		channels.add("SBS-Can");
		channels.add("Ten-Can");
		channels.add("WIN-Can");
		channels.add("SBSTWO-NSW");
		channels.add("One-NSW");
		channels.add("GO");
		Updater updater = ApplicationInjector.getInjector().getInstance(
				Updater.class);
		updater.update(channels);
	}

}
