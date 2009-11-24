package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.controller.ControllerListener;
import moten.david.util.tv.ui.client.event.ProgrammeLoaded;
import moten.david.util.tv.ui.client.event.ShowProgramme;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class TitlePanel extends HorizontalPanel {

	public TitlePanel() {
		setStyleName("title");
		// add(new HTML("<h2>TV Recorder</h2>"));
		final Button showProgramme = createMenuItem("Programme");
		add(showProgramme);
		showProgramme.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Application.getInstance().getController().event(
						new ShowProgramme());
			}
		});
		Application.getInstance().getController().addListener(
				ShowProgramme.class, new ControllerListener<ShowProgramme>() {

					@Override
					public void event(ShowProgramme event) {
						showProgramme.setText("Loading...");
					}
				});
		Application.getInstance().getController().addListener(
				ProgrammeLoaded.class,
				new ControllerListener<ProgrammeLoaded>() {

					@Override
					public void event(ProgrammeLoaded event) {
						showProgramme.setText("Programme");
					}
				});
	}

	private Button createMenuItem(String label) {
		Button button = new Button(label);
		button.setStyleName("menuItem");
		return button;
	}

}
