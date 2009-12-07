package moten.david.util.tv.ui.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import moten.david.util.tv.Channel;
import moten.david.util.tv.ChannelsProvider;
import moten.david.util.tv.Util;
import moten.david.util.tv.programme.Programme;
import moten.david.util.tv.programme.ProgrammeItem;
import moten.david.util.tv.programme.ProgrammeProvider;
import moten.david.util.tv.recorder.Recorder;
import moten.david.util.tv.schedule.Schedule;
import moten.david.util.tv.schedule.ScheduleItem;
import moten.david.util.tv.servlet.ApplicationInjector;
import moten.david.util.tv.ui.client.ApplicationService;
import moten.david.util.tv.ui.client.MyChannel;
import moten.david.util.tv.ui.client.MyProgrammeItem;
import moten.david.util.tv.updater.Updater;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
		ApplicationService {

	private static Logger log = Logger.getLogger(ApplicationServiceImpl.class
			.getName());

	@Inject
	private ProgrammeProvider programmeProvider;
	@Inject
	private ChannelsProvider channelsProvider;
	@Inject
	private Recorder recorder;
	@Inject
	private Schedule schedule;

	private final Channel[] allChannels;

	public ApplicationServiceImpl() {
		log.info("injecting members");
		ApplicationInjector.getInjector().injectMembers(this);
		log.info("getting channels");
		allChannels = channelsProvider.getChannels();
		log.info("constructed");
	}

	@Override
	public MyProgrammeItem[] getProgramme(String[] channelIds, Date start,
			Date finish) {
		try {
			Set<ScheduleItem> scheduledItems = schedule.load();
			ArrayList<MyProgrammeItem> list = new ArrayList<MyProgrammeItem>();
			for (String channelId : channelIds) {
				log.info("getting programme for channel " + channelId);
				Channel channel = Util.getChannel(channelId, allChannels);
				Programme items = programmeProvider
						.getProgramme(channel, start);
				for (ProgrammeItem item : items) {
					MyProgrammeItem p = new MyProgrammeItem();
					p.setChannelId(item.getChannelId());
					p.setDescription(item.getDescription());
					p.setStart(item.getStart());
					p.setStop(item.getStop());
					p.setSubTitle(item.getSubTitle());
					p.setTitle(item.getTitle());
					p.setStartTimeInMinutes(getTimeInMinutes(item.getStart()));
					p.setStopTimeInMinutes(getTimeInMinutes(item.getStop()));
					p.setDate(item.getDate());
					p.setActors(item.getActors().toArray(new String[] {}));
					p.setCategories(item.getCategories().toArray(
							new String[] {}));
					p
							.setScheduledForRecording(isScheduled(
									scheduledItems, item));
					if (p.isScheduledForRecording())
						log.info(item.getTitle() + " is scheduled");
					list.add(p);
				}
			}
			log.info("obtained programme");
			return list.toArray(new MyProgrammeItem[] {});
		} catch (RuntimeException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	private boolean isScheduled(Set<ScheduleItem> scheduledItems,
			ProgrammeItem item) {
		for (ScheduleItem scheduledItem : scheduledItems) {
			if (item.getChannelId().equals(scheduledItem.getChannelId()))
				if ((scheduledItem.getStartDate().equals(item.getStart()) && scheduledItem
						.getEndDate().equals(item.getStop()))
						|| between(scheduledItem.getStartDate(), item
								.getStart(), item.getStop())
						|| between(item.getStart(), scheduledItem
								.getStartDate(), scheduledItem.getEndDate())) {
					return true;
				}
		}
		return false;
	}

	private boolean between(Date date, Date start, Date stop) {
		return date.getTime() > start.getTime()
				&& date.getTime() < stop.getTime();
	}

	private int getTimeInMinutes(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		return hours * 60 + minutes;
	}

	@Override
	public void play(String channelId) {
		try {
			recorder.play(channelId);
		} catch (RuntimeException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public synchronized void record(String name, String channelId, Date start,
			Date stop) {
		try {
			log.info("will record " + name + " at " + start);
			ScheduleItem item = new ScheduleItem(name, channelId, start, stop);
			Set<ScheduleItem> scheduledItems = schedule.load();
			scheduledItems.add(item);
			// remove expired items
			Date expiryDate = new Date(System.currentTimeMillis() - 24 * 60
					* 60 * 1000);
			ArrayList<ScheduleItem> removeThese = new ArrayList<ScheduleItem>();
			for (ScheduleItem it : scheduledItems)
				if (item.getEndDate().before(expiryDate))
					removeThese.add(it);
			scheduledItems.removeAll(removeThese);
			// save
			schedule.save(scheduledItems);
			log.info("saved schedule");
		} catch (RuntimeException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void cancel(String channelId, Date start, Date stop) {
		try {
			Set<ScheduleItem> scheduledItems = schedule.load();
			ArrayList<ScheduleItem> removeThese = new ArrayList<ScheduleItem>();
			for (ScheduleItem item : scheduledItems) {
				if (channelId.equals(item.getChannelId())
						&& start.equals(item.getStartDate())
						&& stop.equals(item.getEndDate()))
					removeThese.add(item);
			}
			scheduledItems.removeAll(removeThese);
			schedule.save(scheduledItems);
			log.info("saved schedule");
		} catch (RuntimeException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public MyChannel[] getChannels() {
		try {
			ArrayList<MyChannel> list = new ArrayList<MyChannel>();
			for (Channel channel : channelsProvider.getChannels()) {
				MyChannel c = new MyChannel();
				c.setName(channel.getDisplayName());
				list.add(c);
			}
			return list.toArray(new MyChannel[] {});
		} catch (RuntimeException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void update() {
		Updater.main(null);
	}

	@Override
	public String[] getSelectedChannelIds() {
		return new String[] { "ABC2", "ABC-Can", "Prime-Can", "SBS-Can",
				"One-NSW" };
	}

	@Override
	public void setSelectedChannelIds(String[] channelIds) {
		// TODO Auto-generated method stub

	}

}
