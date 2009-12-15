package au.edu.anu.delibdem.qsort.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import au.edu.anu.delibdem.qsort.Data;

public class ParticipantsPanel extends JPanel {

	private static final long serialVersionUID = -5357822385951586019L;

	public ParticipantsPanel(final Data data) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		LinkButton selectAll = new LinkButton("Select all");
		LinkButton selectNone = new LinkButton("Select none");
		add(selectAll);
		add(selectNone);
		JCheckBoxList list = new JCheckBoxList();
		final String[] participants = data.getParticipants().toArray(
				new String[0]);
		final Object[] checkBoxes = new Object[participants.length];
		for (int i = 0; i < participants.length; i++) {
			final JCheckBox checkBox = new JCheckBox("" + participants[i]);
			checkBox.setSelected(!data.getExcludeParticipants().contains(
					participants[i]));

			checkBoxes[i] = checkBox;
			final Integer finalI = i;
			checkBox.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (checkBox.isSelected())
						data.getExcludeParticipants().remove(
								participants[finalI]);
					else
						data.getExcludeParticipants().add(participants[finalI]);
					EventManager.getInstance().notify(
							new Event(data, Events.DATA_CHANGED));
				}
			});
		}
		list.setListData(checkBoxes);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(scroll);

		// event listeners
		selectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < checkBoxes.length; i++) {
					JCheckBox checkBox = (JCheckBox) checkBoxes[i];
					checkBox.setSelected(true);
					repaint();
				}
			}
		});

		selectNone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < checkBoxes.length; i++) {
					JCheckBox checkBox = (JCheckBox) checkBoxes[i];
					checkBox.setSelected(false);
					repaint();
				}
			}
		});

		// layout
		layout.putConstraint(SpringLayout.NORTH, selectAll, 5,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, selectAll, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, selectNone, 5,
				SpringLayout.SOUTH, selectAll);
		layout.putConstraint(SpringLayout.WEST, selectNone, 0,
				SpringLayout.WEST, selectAll);
		layout.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.SOUTH,
				selectNone);
		layout.putConstraint(SpringLayout.SOUTH, scroll, 0, SpringLayout.SOUTH,
				this);
		layout.putConstraint(SpringLayout.WEST, scroll, 5, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, scroll, 0, SpringLayout.EAST,
				this);

	}
}
