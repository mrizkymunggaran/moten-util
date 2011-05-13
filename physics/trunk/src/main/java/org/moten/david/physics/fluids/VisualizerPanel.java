package org.moten.david.physics.fluids;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VisualizerPanel extends JPanel {

	private final VisualizerData data;

	public VisualizerPanel(VisualizerData data) {
		this.data = data;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = getSize().width;
		int h = getSize().height;
		for (int i = 0; i <= w; i++) {
			for (int j = 0; j <= h; j++) {
				float value = data.getValue((float) i / w, (float) j / h);
				Color color = getColor(value);
				g.setColor(color);
				g.drawLine(i, j, i + 1, j + 1);
			}
		}
	}

	private Color getColor(float value) {
		float hue = 1 - 0.9f * value - 0.1f;
		float saturation = 1.0f;
		float brightness = 1.0f;
		Color color = Color.getHSBColor(hue, saturation, brightness);
		return color;
	}

	public static void main(String[] args) {
		VisualizerPanel p = new VisualizerPanel(new VisualizerData() {
			@Override
			public float getValue(float x, float y) {
				return x * y;
			}
		});
		final JFrame frame = new JFrame("Data Visualizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		frame.getContentPane().add(p);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}