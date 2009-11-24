package moten.david.util.tv;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Configuration {
	public File getDataDirectory() {
		File dir = new File(System.getProperty("user.home")
				+ "/.moten/recorder");
		if (!dir.exists() && !dir.mkdirs())
			throw new RuntimeException("could not create data directory " + dir);
		return dir;
	}

	public File getDataList() {
		return new File(getDataDirectory(), "datalist.xml");
	}

	public String getDataListUrlZipped() {
		return "http://www.oztivo.net/xmltv/datalist.xml.gz";
	}

	public File getProgrammeFile(Channel station, Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return new File(getDataDirectory(), station.getId() + "_"
				+ df.format(date) + ".xml");
	}

	public URL getProgrammeUrl(Channel station, Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (station.getBaseUrls().size() == 0)
			throw new RuntimeException("station does not have a base url! "
					+ station);
		try {
			return new URL(station.getBaseUrls().get(0) + station.getId() + "_"
					+ df.format(date) + ".xml");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public File getScheduleFile() {
		return new File(getDataDirectory(), "schedule.obj");
	}

	public File getRecordingsFolder() {
		return new File(getDataDirectory(), "recordings");
	}
}
