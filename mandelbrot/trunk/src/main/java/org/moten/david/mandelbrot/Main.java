package org.moten.david.mandelbrot;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(740, 740);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		final Color brown = new Color(205, 183, 158);
		final Color salmon = new Color(250, 128, 114);
		List<Color> browny = new ArrayList<Color>() {
			{
				add(brown);
				add(Color.white);
				add(salmon);
				add(Color.black);
			}
		};
		List<Color> wikipedia = new ArrayList<Color>() {
			{
				add(Color.blue);
				add(Color.white);
				add(Color.yellow);
				add(Color.black);
			}
		};
		MandelbrotFractal fractal = new MandelbrotFractal(512, 1, wikipedia);
		frame.getContentPane().add(fractal);
		// frame.pack();
		frame.setVisible(true);
	}
}
