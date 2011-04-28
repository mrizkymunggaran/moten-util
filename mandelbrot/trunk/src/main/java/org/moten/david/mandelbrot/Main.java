package org.moten.david.mandelbrot;

import java.awt.GridLayout;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		MandelbrotFractal fractal = new MandelbrotFractal();
		frame.getContentPane().add(fractal);
		frame.pack();
		frame.setVisible(true);
	}
}
