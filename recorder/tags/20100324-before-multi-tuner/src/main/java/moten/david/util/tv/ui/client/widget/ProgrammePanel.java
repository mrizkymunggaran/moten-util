package moten.david.util.tv.ui.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.ApplicationService;
import moten.david.util.tv.ui.client.ApplicationServiceAsync;
import moten.david.util.tv.ui.client.MyProgrammeItem;
import moten.david.util.tv.ui.client.controller.ControllerListener;
import moten.david.util.tv.ui.client.event.ProgrammeLoaded;
import moten.david.util.tv.ui.client.event.ShowProgramme;
import moten.david.util.tv.ui.client.event.Status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgrammePanel extends VerticalPanel {

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final ApplicationServiceAsync applicationService = GWT
			.create(ApplicationService.class);
	private final AsyncCallback<MyProgrammeItem[]> getProgrammeCallback;
	private FlexTable table;
	private final List<String> channels = new ArrayList<String>();
	private final long minuteMs = 60000;
	private FlexTable oldTable;
	private final Widget loading;

	public ProgrammePanel() {
		Application.getInstance().getController().addListener(
				ShowProgramme.class, createRefreshListener());
		getProgrammeCallback = createGetProgrammeCallback();
		table = new FlexTable();
		table.setStyleName("programme");

		channels.add("SBS-Can");
		channels.add("SBSTWO-NSW");
		channels.add("One-NSW");
		channels.add("ABC-Can");
		channels.add("ABC2");
		channels.add("ABC3");
		channels.add("Prime-Can");
		channels.add("Ten-Can");
		channels.add("WIN-Can");
		channels.add("GO");

		loading = new Label("Loading...");
	}

	private AsyncCallback<MyProgrammeItem[]> createGetProgrammeCallback() {
		return new AsyncCallback<MyProgrammeItem[]>() {

			@Override
			public void onFailure(Throwable t) {
				add(new HTML(t.toString()));
				Application.getInstance().getController().event(
						new ProgrammeLoaded());
			}

			@Override
			public void onSuccess(MyProgrammeItem[] items) {
				MyProgrammeItem current = null;
				int currentCol = -99999;
				try {
					// each cell is 5 mins wide
					final int cellWidthMinutes = 5;

					int totalExtraSpan = 0;
					String lastChannelId = null;
					Date bestStartTime = getBestStartTime(items);

					int index = 0;
					// insert the items into the table
					for (final MyProgrammeItem item : items) {
						current = item;
						if (item.getStop().after(bestStartTime)) {
							// reset span count if channel has changed because
							// that means we have moved to the another row
							if (!item.getChannelId().equals(lastChannelId))
								totalExtraSpan = 0;

							// get the row corresponding to the channel
							int row = channels.indexOf(item.getChannelId());
							if (row < 0)
								throw new RuntimeException(
										"channel id not found: "
												+ item.getChannelId());

							// set the row channel label
							table.setWidget(row, 0, getChannelLabel(item));

							int col = (int) ((item.getStart().getTime() - bestStartTime
									.getTime())
									/ (cellWidthMinutes * minuteMs) + 1);
							col -= totalExtraSpan;
							currentCol = col;
							// calculate stop time based on next programme item
							// start time
							Date stopTime = item.getStop();
							if (false
									&& index < items.length - 1
									&& items[index + 1].getChannelId().equals(
											item.getChannelId()))
								stopTime = items[index + 1].getStart();
							int span = (int) ((stopTime.getTime() - item
									.getStart().getTime()) / (minuteMs * cellWidthMinutes));
							totalExtraSpan += span - 1;

							table.setWidget(row, col, createItemWidget(item));
							table.getFlexCellFormatter().setColSpan(row, col,
									span);
							table.getRowFormatter().setStyleName(row,
									"programmeRow");
							table.getFlexCellFormatter().setStyleName(row, col,
									"programmeItem");
							lastChannelId = item.getChannelId();
						}
						index++;
					}

					for (int row = 0; row < table.getRowCount(); row++) {
						table.getCellFormatter().setStyleName(row, 0,
								"channelCell");
						int span = 1;
						int col = 1;
						while (col < table.getCellCount(row)
								&& table.getWidget(row, col) == null) {
							table.getFlexCellFormatter().setStyleName(row, col,
									"programmeItemEmpty");
							span++;
							col++;
						}
					}
					remove(loading);
					remove(oldTable);
					add(table);
					Application.getInstance().getController().event(
							new ProgrammeLoaded());
				} catch (RuntimeException e) {
					// ByteArrayOutputStream bytes = new
					// ByteArrayOutputStream();
					// e.printStackTrace(new PrintStream(bytes));
					e.printStackTrace();
					int line = e.getStackTrace()[0].getLineNumber();
					add(new Label("Programme callback error: " + e.toString()
							+ " current=" + current.getChannelId()
							+ current.getTitle() + current.getStart() + " col="
							+ currentCol + " stackTrace line no = " + line));
				}
			}

			private Widget getChannelLabel(MyProgrammeItem item) {
				Label label = new Label(item.getChannelId());
				label.setStyleName("channel");
				return label;
			}

			private Widget createItemWidget(MyProgrammeItem item) {
				VerticalPanel vp = new VerticalPanel();
				vp.setStyleName("noBorder");
				Label labelTime = new Label(getStartTimeString(item));

				vp.add(labelTime);
				DisclosurePanel disclosureTitle = new DisclosurePanel();
				Label labelTitle = new Label(item.getTitle());
				if (item.isHighlighted())
					labelTitle.setStyleName("itemTitleHighlighted");
				else
					labelTitle.setStyleName("itemTitle");
				disclosureTitle.setHeader(labelTitle);
				disclosureTitle.setContent(getContent(item, labelTime));
				vp.add(disclosureTitle);

				return vp;
			}

			private boolean isOnNow(MyProgrammeItem item) {
				Date now = new Date();
				boolean isOnNow = item.getStart().before(now)
						&& item.getStop().after(now);
				return isOnNow;
			}

			private String delimited(String[] c, String delimiter) {
				StringBuffer s = new StringBuffer();
				for (String str : c) {
					if (s.length() > 0)
						s.append(delimiter);
					s.append(str);
				}
				return s.toString();
			}

			private Widget getContent(final MyProgrammeItem item,
					final Label labelTime) {
				VerticalPanel content = new VerticalPanel();
				Label text = new Label();
				long minutes = (item.getStop().getTime() - item.getStart()
						.getTime())
						/ minuteMs;
				StringBuffer s = new StringBuffer();
				s.append(item.getDescription() + " "
						+ (item.getDate() == null ? "" : item.getDate() + ", ")
						+ minutes + "mins");

				if (item.getCategories().length > 0) {
					s.append(" [");
					s.append(delimited(item.getCategories(), ", "));
					s.append("]");
				}

				if (item.getActors().length > 0) {
					s.append("\nActors: ");
					s.append(delimited(item.getActors(), ", "));
				}

				text.setText(s.toString());
				text.setStyleName("itemDescription");
				content.add(text);

				HorizontalPanel p = new HorizontalPanel();
				Button play = new Button("Play");
				play.setStyleName("play");
				if (isOnNow(item))
					p.add(play);
				final DisclosurePanel record = new DisclosurePanel();
				p.add(record);
				Label recordLabel = new Label("Record");
				recordLabel.setStyleName("record");
				record.setHeader(recordLabel);

				Button about = new Button("About");
				about.setStyleName("about");
				p.add(about);
				about.addClickHandler(createAboutClickHandler(item));

				// recording content
				Button cancel = new Button("Cancel");

				final Panel recordingContent = new HorizontalPanel();
				recordingContent.add(cancel);

				// not recording content
				final Panel notRecordingContent = new HorizontalPanel();
				ListBox quality = new ListBox();
				quality.addItem("Normal quality");
				quality.addItem("High quality");
				notRecordingContent.add(quality);
				Button recordButton = new Button("Record");
				notRecordingContent.add(recordButton);
				record.setContent(notRecordingContent);

				Runnable updateRecordContent = new Runnable() {
					public void run() {
						// set content
						if (item.isScheduledForRecording()) {
							record.setContent(recordingContent);
						} else {
							record.setContent(notRecordingContent);
						}
						if (item.isScheduledForRecording())
							labelTime.setStyleName("scheduled");
						else if (isOnNow(item))
							labelTime.setStyleName("currentTime");
						else
							labelTime.setStyleName("time");
					}
				};

				cancel.addClickHandler(createCancelHandler(cancel, item,
						updateRecordContent));
				recordButton.addClickHandler(createRecordClickHandler(
						recordButton, item, updateRecordContent));
				updateRecordContent.run();
				content.add(p);
				play
						.addClickHandler(createPlayClickHandler(item
								.getChannelId()));
				return content;
			}

			private ClickHandler createAboutClickHandler(
					final MyProgrammeItem item) {
				return new ClickHandler() {
					@Override
					public void onClick(ClickEvent arg0) {
						if (item.getTitle() != null) {
							String url = "http://www.imdb.com/find?s=all&q="
									+ item.getTitle();
							Window.open(url, null, null);
						}
					}
				};
			}

			private String getStartTimeString(MyProgrammeItem item) {
				String startTime = (item.getStartTimeInMinutes() / 60) + "";
				if (startTime.length() == 1)
					startTime = "0" + startTime;
				startTime += ":";
				String minutes = (item.getStartTimeInMinutes() % 60) + "";
				if (minutes.length() == 1)
					minutes = "0" + minutes;
				startTime += minutes;
				return startTime;
			}

			private Date getBestStartTime(MyProgrammeItem[] items) {
				Date bestStartTime = null;
				for (final MyProgrammeItem item : items) {
					if (bestStartTime == null
							|| item.getStart().before(bestStartTime))
						bestStartTime = item.getStart();
				}
				if (bestStartTime == null)
					bestStartTime = new Date();
				return bestStartTime;
			}

		};
	}

	private ClickHandler createCancelHandler(final Button cancel,
			final MyProgrammeItem item, final Runnable updateRecordContent) {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				cancel.setEnabled(false);

				applicationService.cancel(item.getChannelId(), item.getStart(),
						item.getStop(), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable t) {
								add(new Label(t.getMessage()));
								cancel.setEnabled(true);
								item.setScheduledForRecording(true);
								cancel.setText(t.getMessage());
								updateRecordContent.run();
							}

							@Override
							public void onSuccess(Void arg0) {
								cancel.setEnabled(true);
								item.setScheduledForRecording(false);
								updateRecordContent.run();
							}
						});
			}
		};
	}

	private ClickHandler createPlayClickHandler(final String channelId) {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				applicationService.play(channelId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable arg0) {
						add(new Label(arg0.getMessage()));
					}

					@Override
					public void onSuccess(Void arg0) {
					}
				});
			}
		};
	}

	private ClickHandler createRecordClickHandler(final Button recordButton,
			final MyProgrammeItem item, final Runnable updateRecordContent) {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				recordButton.setEnabled(false);

				applicationService.record(item.getTitle(), item.getChannelId(),
						item.getStart(), item.getStop(),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable arg0) {
								add(new Label(arg0.getMessage()));
								recordButton.setEnabled(true);
								item.setScheduledForRecording(false);
								updateRecordContent.run();
							}

							@Override
							public void onSuccess(Void arg0) {
								recordButton.setEnabled(true);
								item.setScheduledForRecording(true);
								updateRecordContent.run();
							}
						});
			}
		};
	}

	private ControllerListener<ShowProgramme> createRefreshListener() {
		return new ControllerListener<ShowProgramme>() {

			@Override
			public void event(ShowProgramme event) {
				Application.getInstance().getController().event(
						new Status("Loading..."));
				refresh();
			}

		};
	}

	public void refresh() {
		clear();
		add(loading);
		oldTable = table;
		table = new FlexTable();
		table.setStyleName("programme");
		Date now = new Date();
		applicationService.getProgramme(channels.toArray(new String[] {}), now,
				new Date(now.getTime() + 24 * 3600 * 1000),
				getProgrammeCallback);
	}
}
