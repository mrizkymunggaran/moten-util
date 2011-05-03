package org.moten.david.mandelbrot;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
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
		final MandelbrotFractal fractal = new MandelbrotFractal(512, 1,
				wikipedia);
		Runnable onRedraw = new Runnable() {
			int number = 0;

			public void run() {
				BufferedImage image = fractal.getImage();
				FileOutputStream fos;
				try {
					fos = new FileOutputStream("target/m" + (++number) + ".png");
					ImageIO.write(image, "png", fos);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		fractal.setOnImageRedraw(onRedraw);
		frame.getContentPane().add(fractal);
		// frame.pack();
		frame.setVisible(true);

		// http://fractaljourney.blogspot.com/2010/01/mandelbrot-ultra-zoom-5-21e275.html
	}
}
