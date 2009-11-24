package moten.david.util.tv;

import java.util.Date;

public class DataFor {
	@Override
	public String toString() {
		return "DataFor [date=" + date + ", lastModified=" + lastModified + "]";
	}

	private Date lastModified;
	private Date date;

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
