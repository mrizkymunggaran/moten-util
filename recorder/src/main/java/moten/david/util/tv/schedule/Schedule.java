package moten.david.util.tv.schedule;

import java.util.Set;

public interface Schedule {

	Set<ScheduleItem> load();

	void save(Set<ScheduleItem> scheduleItems);
}
