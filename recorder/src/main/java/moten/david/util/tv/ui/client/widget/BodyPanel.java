package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.controller.ControllerListener;
import moten.david.util.tv.ui.client.event.ShowProgramme;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BodyPanel extends VerticalPanel {

	private final ProgrammePanel programmePanel;

	public BodyPanel() {
		setStyleName("body");
		Application.getInstance().getController().addListener(
				ShowProgramme.class, createShowProgrammeListener());
		programmePanel = new ProgrammePanel();
		setContent(programmePanel);
		Application.getInstance().getController().event(new ShowProgramme());
	}

	private ControllerListener<ShowProgramme> createShowProgrammeListener() {
		return new ControllerListener<ShowProgramme>() {
			@Override
			public void event(ShowProgramme event) {
				setContent(programmePanel);
			}
		};
	}

	public void setContent(Panel panel) {
		this.clear();
		add(panel);
	}

}
