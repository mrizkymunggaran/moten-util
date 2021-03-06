package moten.david.util.tv.ui.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {
	void getProgramme(String[] channelIds, Date start, Date finish,
			AsyncCallback<MyProgrammeItem[]> callback);

	void play(String channelId, AsyncCallback<Void> callback);

	void record(String name, String channelId, Date start, Date stop,
			AsyncCallback<Void> callback);

	void cancel(String channelId, Date start, Date stop,
			AsyncCallback<Void> callback);

	void getChannels(AsyncCallback<MyChannel[]> callback);

	void getSelectedChannelIds(AsyncCallback<String[]> callback);

	void setSelectedChannelIds(String[] channelIds, AsyncCallback<Void> callback);

	void update(AsyncCallback<Void> callback);
}
