package moten.david.util.tv;

import java.util.ArrayList;
import java.util.List;

public class Channel {

	@Override
	public String toString() {
		return "Station [id=" + id + ", displayName=" + displayName
				+ ", baseUrls=" + baseUrls + ", dataFor=" + dataFor + "]";
	}

	private String id;
	private String displayName;
	private final List<String> baseUrls = new ArrayList<String>();
	private final List<DataFor> dataFor = new ArrayList<DataFor>();
	private String alias;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getBaseUrls() {
		return baseUrls;
	}

	public List<DataFor> getDataFor() {
		return dataFor;
	}

}
