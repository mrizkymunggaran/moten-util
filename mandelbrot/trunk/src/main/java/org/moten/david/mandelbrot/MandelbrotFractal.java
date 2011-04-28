package org.moten.david.mandelbrot;

/** {{{ http://code.activestate.com/recipes/577158/ (r1) */
// <applet code="MandelbrotFractal" width=800 height=600></applet>
// MandelbrotFractal.java (FB - 201003276)
// N Multi-threaded!

import static java.math.BigDecimal.valueOf;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.math.BigDecimal;

import javax.swing.JPanel;

public class MandelbrotFractal extends JPanel {

	final int maxThr = 10; // number of threads to run

	// drawing area (must be xa<xb and ya<yb)
	BigDecimal xa = valueOf(-2.0);
	BigDecimal xb = valueOf(1.0);
	BigDecimal ya = valueOf(-1.5);
	BigDecimal yb = valueOf(1.5);

	public MandelbrotFractal() {
		setPreferredSize(new Dimension(1000, 700));
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				double propX = (double) e.getPoint().x / getSize().width;
				double propY = (double) e.getPoint().y / getSize().height;
				double zoomFactor = 4;
				{
					BigDecimal newDiffX = xb.subtract(xa).divide(
							valueOf(zoomFactor));
					xa = xb.subtract(xa).multiply(valueOf(propX)).add(xa)
							.subtract(newDiffX.divide(valueOf(2)));
					xb = xa.add(newDiffX);
				}
				{
					BigDecimal newDiffY = yb.subtract(ya).divide(
							valueOf(zoomFactor));
					ya = yb.subtract(ya).multiply(valueOf(propY)).add(ya)
							.subtract(newDiffY.divide(valueOf(2)));
					yb = ya.add(newDiffY);
				}
				repaint();
			}

		});
	}

	private Image createFractalImage(int w, int h) {
		int alpha = 255;
		int[] pix = new int[w * h];

		paintFractal(maxThr, pix, w, h, xa, ya, xb, yb, alpha);

		return createImage(new MemoryImageSource(w, h, pix, 0, w));
	}

	@Override
	public void paintComponent(Graphics g) {
		System.out.println("painting component " + getSize());
		g.clearRect(0, 0, getSize().width, getSize().height);
		Image img = createFractalImage(getSize().width, getSize().height);
		g.drawImage(img, 0, 0, this);
	}

	public void paintFractal(int maxThr, int[] pix, int w, int h,
			BigDecimal xa, BigDecimal ya, BigDecimal xb, BigDecimal yb,
			int alpha) {
		long startTime = System.currentTimeMillis();

		MandelbrotFractalThread[] m = new MandelbrotFractalThread[maxThr];
		for (int i = 0; i < maxThr; i++) {
			m[i] = new MandelbrotFractalThread(i, maxThr, pix, w, h, xa, ya,
					xb, yb, alpha);
			m[i].start();
		}

		// wait until all threads finished
		boolean stop;
		do {
			stop = true;
			for (int j = 0; j < maxThr; j++) {
				if (m[j].isAlive()) {
					stop = false;
				}
			}
		} while (!stop);

		System.out.println("Number of threads: " + maxThr);
		long timeInMillis = System.currentTimeMillis() - startTime;
		System.out.println("Run Time in Millis: " + timeInMillis);
	}

}
/** end of http://code.activestate.com/recipes/577158/ }}} */
