package moten.david.util.tv.ui;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;

import moten.david.util.tv.programme.ProgrammeProvider;
import moten.david.util.tv.recorder.Recorder;

public class ProgrammePanel extends JPanel {

	private final ProgrammeProvider programmeProvider;
	private final Recorder recorder;
	private final List<String> channels;

	public ProgrammePanel(ProgrammeProvider programmeProvider,
			Recorder recorder, List<String> channels) {
		super(new GridBagLayout());
		this.programmeProvider = programmeProvider;
		this.recorder = recorder;
		this.channels = channels;

	}

	private void update() {
		removeAll();
		for (String channel : channels) {
			//			
		}
	}
}
