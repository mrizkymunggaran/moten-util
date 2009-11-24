package moten.david.util.tv.servlet;

import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

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

	public RecordJob() {
		ApplicationInjector.getInjector().injectMembers(this);
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		log.info("executing job");
		// for each item in the schedule
		Set<ScheduleItem> scheduleItems = schedule.load();
		Date now = new Date();
		for (ScheduleItem item : scheduleItems) {
			// if the item should be on now or if the item starts now
			if (item.getStartDate().getTime() <= now.getTime()
					&& item.getEndDate().getTime() > now.getTime()) {
				// if the item is not recording already
				if (!recorder.isRecording(item))
					// start recording
					recorder.startRecording(item);
			} else if (recorder.isRecording(item)) {
				recorder.stopRecording(item);
			}
		}
		log.info("finished job");

	}
}
