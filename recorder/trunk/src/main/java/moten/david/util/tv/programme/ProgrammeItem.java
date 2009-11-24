package moten.david.util.tv.programme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgrammeItem {
	public Date getStart() {
		return start;
	}

	@Override
	public String toString() {
		return "ScheduleItem [channelId=" + channelId + ", title=" + title
				+ ", subTitle=" + subTitle + ", start=" + start + ", stop="
				+ stop + ", description=" + description + ", categories="
				+ categories + "]";
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

	public List<String> getCategories() {
		return categories;
	}

	public int getDurationMinutes() {
		return (int) ((stop.getTime() - start.getTime()) / 1000 / 60);
	}

	private Date start;
	private Date stop;
	private String channelId;
	private String title;
	private String subTitle;
	private String description;
	private final List<String> categories = new ArrayList<String>();

}
