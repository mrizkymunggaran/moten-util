package moten.david.util.tv.schedule;

import java.io.Serializable;
import java.util.Date;

/**
 * An item scheduled for recording
 * 
 * @author dave
 * 
 */
public class ScheduleItem implements Serializable {
	final String name;
	final String channelId;
	final Date startDate;
	final Date endDate;

	public String getName() {
		return name;
	}

	public ScheduleItem(String name, String channelId, Date startDate,
			Date endDate) {
		super();
		this.name = name;
		this.channelId = channelId;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getChannelId() {
		return channelId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduleItem other = (ScheduleItem) obj;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
}
