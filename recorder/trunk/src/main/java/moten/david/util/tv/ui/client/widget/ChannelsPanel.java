package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.ApplicationService;
import moten.david.util.tv.ui.client.ApplicationServiceAsync;
import moten.david.util.tv.ui.client.MyChannel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChannelsPanel extends HorizontalPanel {

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final ApplicationServiceAsync applicationService = GWT
			.create(ApplicationService.class);

	private final ListBox channelsList = new ListBox();

	public ChannelsPanel() {
		channelsList.setVisibleItemCount(20);
		add(channelsList);
		add(createButtons());
		applicationService.getChannels(createGetChannelsCallback());

	}

	private Widget createButtons() {
		VerticalPanel panel = new VerticalPanel();
		panel.setHorizontalAlignment(ALIGN_CENTER);
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		Button up = new Button("Up");
		Button down = new Button("Down");
		Button remove = new Button("Remove");
		Button add = new Button("Add..");
		panel.add(add);
		panel.add(remove);
		panel.add(up);
		panel.add(down);
		return panel;
	}

	private AsyncCallback<MyChannel[]> createGetChannelsCallback() {

		return new AsyncCallback<MyChannel[]>() {

			@Override
			public void onFailure(Throwable throwable) {
				add(new Label(throwable.getMessage()));
			}

			@Override
			public void onSuccess(MyChannel[] channels) {
				reset(channels);
			}

			private void reset(MyChannel[] channels) {
				channelsList.clear();
				for (MyChannel channel : channels) {
					channelsList.addItem(channel.getName());
				}
			}
		};
	}

}
