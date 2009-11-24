package moten.david.util.tv.ui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import moten.david.util.tv.programme.ProgrammeItem;

public class ProgrammeItemPanel extends JPanel {

	private final ProgrammeItem item;

	public ProgrammeItem getItem() {
		return item;
	}

	public ProgrammeItemPanel(ProgrammeItem item) {
		super(new GridLayout(1, 1));
		this.item = item;
		add(new JLabel(item.getTitle()));
	}
}
