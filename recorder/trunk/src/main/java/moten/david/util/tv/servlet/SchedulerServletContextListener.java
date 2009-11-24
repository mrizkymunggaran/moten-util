package moten.david.util.tv.servlet;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

public class SchedulerServletContextListener implements ServletContextListener {

	private static Logger log = Logger
			.getLogger(SchedulerServletContextListener.class.getName());
	private Scheduler scheduler;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
			createUpdateJob(scheduler);
			createRecordJob(scheduler);
		} catch (RuntimeException e) {
			log.severe(e.getMessage());
			throw e;
		} catch (SchedulerException e) {
			log.severe(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void createRecordJob(Scheduler scheduler) throws SchedulerException {
		JobDetail jobDetail = new JobDetail("recordJob", null, RecordJob.class);
		// run every minute
		Trigger trigger = TriggerUtils.makeMinutelyTrigger();
		trigger.setName("recordTrigger");
		scheduler.scheduleJob(jobDetail, trigger);
	}

	private void createUpdateJob(Scheduler scheduler) throws SchedulerException {
		JobDetail jobDetail = new JobDetail("updaterJob", null,
				UpdaterJob.class);
		// run at 04:43 every day
		Trigger trigger = TriggerUtils
				.makeDailyTrigger("updaterTrigger", 4, 43);
		scheduler.scheduleJob(jobDetail, trigger);

	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			if (scheduler != null)
				scheduler.shutdown();
		} catch (SchedulerException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
	}

}
