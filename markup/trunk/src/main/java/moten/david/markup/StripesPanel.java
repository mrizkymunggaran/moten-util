package moten.david.markup;

import java.awt.Graphics;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

import com.google.inject.Inject;

public class StripesPanel extends JPanel {

	private final JEditorPane text;

	@Inject
	public StripesPanel(JEditorPane text) {
		this.text = text;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawString("Hi", 20, 20);
	}

}
