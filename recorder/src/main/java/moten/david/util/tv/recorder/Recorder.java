package moten.david.util.tv.recorder;

import moten.david.util.tv.schedule.ScheduleItem;

public interface Recorder {

	boolean isRecording(ScheduleItem item);

	void startRecording(ScheduleItem item);

	void stopRecording(ScheduleItem item);

	void play(String channelId);

}
