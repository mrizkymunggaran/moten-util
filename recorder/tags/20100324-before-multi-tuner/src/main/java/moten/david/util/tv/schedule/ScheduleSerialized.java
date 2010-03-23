package moten.david.util.tv.schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import moten.david.util.tv.Configuration;

import com.google.inject.Inject;

public class ScheduleSerialized implements Schedule {

	private static Logger log = Logger.getLogger(ScheduleSerialized.class
			.getName());
	private final File scheduleFile;
	private final long expiryTime;

	@Inject
	public ScheduleSerialized(Configuration configuration) {
		scheduleFile = configuration.getScheduleFile();
		expiryTime = configuration.getScheduleItemExpiryTimeMs();
	}

	@Override
	public Set<ScheduleItem> load() {
		try {
			log.info("loading schedule from " + scheduleFile);
			if (!scheduleFile.exists())
				return new HashSet<ScheduleItem>();
			else {
				ObjectInputStream is = new ObjectInputStream(
						new FileInputStream(scheduleFile));
				Set<ScheduleItem> set = (Set<ScheduleItem>) is.readObject();
				is.close();
				removeExpiredItems(set);
				return set;
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void removeExpiredItems(Set<ScheduleItem> set) {
		Set<ScheduleItem> removeThese = new HashSet<ScheduleItem>();
		for (ScheduleItem item : set) {
			if (item.getEndDate().before(
					new Date(System.currentTimeMillis() - expiryTime)))
				removeThese.add(item);
		}
		set.removeAll(removeThese);
	}

	@Override
	public void save(Set<ScheduleItem> scheduleItems) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(scheduleFile));
			oos.writeObject(scheduleItems);
			oos.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
