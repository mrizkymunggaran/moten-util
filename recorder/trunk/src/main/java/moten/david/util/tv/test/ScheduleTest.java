package moten.david.util.tv.test;

import java.util.Date;
import java.util.Set;

import moten.david.util.tv.Configuration;
import moten.david.util.tv.schedule.Schedule;
import moten.david.util.tv.schedule.ScheduleItem;
import moten.david.util.tv.schedule.ScheduleSerialized;

public class ScheduleTest {

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		Schedule schedule = new ScheduleSerialized(configuration);
		Set<ScheduleItem> items = schedule.load();
		items.add(new ScheduleItem("Test", "ABC", new Date(), new Date(System
				.currentTimeMillis() + 60 * 60 * 1000)));
		schedule.save(items);
	}
}