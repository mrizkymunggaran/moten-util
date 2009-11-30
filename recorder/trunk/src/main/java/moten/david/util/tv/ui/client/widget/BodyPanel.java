package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.controller.ControllerListener;
import moten.david.util.tv.ui.client.event.ShowProgramme;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BodyPanel extends VerticalPanel {

	private final ProgrammePanel programmePanel;

	public BodyPanel() {
		setStyleName("body");
		Application.getInstance().getController().addListener(
				ShowProgramme.class, createShowProgrammeListener());
		TabPanel tabs = new TabPanel();
		tabs.setStyleName("tabs");
		programmePanel = new ProgrammePanel();
		tabs.add(programmePanel, "Programme");
		tabs.add(new Label("TODO"), "Schedule");
		tabs.add(new Label("TODO"), "Settings");
		tabs.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem() == 0) {
					Application.getInstance().getController().event(
							new ShowProgramme());
				}
			}
		});
		add(tabs);
		tabs.selectTab(0);
	}

	private ControllerListener<ShowProgramme> createShowProgrammeListener() {
		return new ControllerListener<ShowProgramme>() {
			@Override
			public void event(ShowProgramme event) {
				programmePanel.refresh();
			}
		};
	}

}
