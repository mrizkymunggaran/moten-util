package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.ApplicationService;
import moten.david.util.tv.ui.client.ApplicationServiceAsync;
import moten.david.util.tv.ui.client.MyChannel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChannelsPanel extends VerticalPanel {

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final ApplicationServiceAsync applicationService = GWT
			.create(ApplicationService.class);

	public ChannelsPanel() {
		applicationService.getChannels(createGetChannelsCallback());
	}

	private AsyncCallback<MyChannel[]> createGetChannelsCallback() {

		return new AsyncCallback<MyChannel[]>() {

			@Override
			public void onFailure(Throwable throwable) {

			}

			@Override
			public void onSuccess(MyChannel[] channels) {
				clear();
				ListBox list = new ListBox();
				list.setVisibleItemCount(20);
				for (MyChannel channel : channels) {
					list.addItem(channel.getName());
				}
				add(list);
			}
		};
	}

}
