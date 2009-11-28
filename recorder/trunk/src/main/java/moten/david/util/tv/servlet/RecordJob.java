package moten.david.util.tv.servlet;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import moten.david.util.tv.Configuration;
import moten.david.util.tv.recorder.Recorder;
import moten.david.util.tv.schedule.Schedule;
import moten.david.util.tv.schedule.ScheduleItem;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.inject.Inject;

public class RecordJob implements Job {

	private static Logger log = Logger.getLogger(RecordJob.class.getName());
	@Inject
	private Schedule schedule;

	@Inject
	private Recorder recorder;

	@Inject
	private Configuration configuration;

	public RecordJob() {
		ApplicationInjector.getInjector().injectMembers(this);
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		log.info("executing job");
		// for each item in the schedule
		Set<ScheduleItem> scheduleItems = schedule.load();
		Date now = new Date();
		Set<ScheduleItem> stopThese = new HashSet<ScheduleItem>();
		Set<ScheduleItem> startThese = new HashSet<ScheduleItem>();
		for (ScheduleItem item : scheduleItems) {
			// if the item should be on now or if the item starts now
			if (item.getStartDate().getTime() <= now.getTime()
					&& item.getEndDate().getTime() > now.getTime()) {
				// if the item is not recording already
				if (!recorder.isRecording(item))
					// start recording
					startThese.add(item);
			} else if (recorder.isRecording(item)
					&& item.getEndDate().getTime()
							+ configuration.getExtraTimeMs() > now.getTime()) {
				stopThese.add(item);
			}
		}
		for (ScheduleItem item : stopThese)
			recorder.stopRecording(item);
		boolean singleTuner = configuration.getTunersCount() == 1;
		if (singleTuner) {
			// start the latest scheduled recording
			Date latestStartDate = null;
			ScheduleItem latestItem = null;
			for (ScheduleItem item : startThese)
				if (latestStartDate == null
						|| item.getStartDate().after(latestStartDate)) {
					latestStartDate = item.getStartDate();
					latestItem = item;
				}
			if (latestItem != null)
				recorder.startRecording(latestItem);
		} else
			for (ScheduleItem item : startThese)
				recorder.startRecording(item);

		log.info("finished job");

	}
}
