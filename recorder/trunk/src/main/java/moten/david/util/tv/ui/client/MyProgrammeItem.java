package moten.david.util.tv.ui.client;

import java.io.Serializable;
import java.util.Date;

public class MyProgrammeItem implements Serializable {
	private Date start;
	private Date stop;
	private String channelId;
	private String title;
	private String subTitle;
	private String description;
	private String[] categories;
	private int startTimeInMinutes;
	private int stopTimeInMinutes;
	private boolean scheduledForRecording;
	private Quality quality;

	public boolean isScheduledForRecording() {
		return scheduledForRecording;
	}

	public void setScheduledForRecording(boolean scheduledForRecording) {
		this.scheduledForRecording = scheduledForRecording;
	}

	public Quality getQuality() {
		return quality;
	}

	public void setQuality(Quality quality) {
		this.quality = quality;
	}

	public int getStartTimeInMinutes() {
		return startTimeInMinutes;
	}

	public void setStartTimeInMinutes(int startTimeInMinutes) {
		this.startTimeInMinutes = startTimeInMinutes;
	}

	public int getStopTimeInMinutes() {
		return stopTimeInMinutes;
	}

	public void setStopTimeInMinutes(int stopTimeInMinutes) {
		this.stopTimeInMinutes = stopTimeInMinutes;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStop() {
		return stop;
	}

	public void setStop(Date stop) {
		this.stop = stop;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getCategories() {
		return categories;
	}
}
