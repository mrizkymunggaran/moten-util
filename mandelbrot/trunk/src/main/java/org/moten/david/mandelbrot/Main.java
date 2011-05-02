package org.moten.david.mandelbrot;

import java.awt.GridLayout;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(740, 740);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		MandelbrotFractal fractal = new MandelbrotFractal(1024, 1);
		frame.getContentPane().add(fractal);
		// frame.pack();
		frame.setVisible(true);
	}
}
