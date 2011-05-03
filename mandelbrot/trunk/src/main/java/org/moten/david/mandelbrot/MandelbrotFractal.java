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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class MandelbrotFractal extends JPanel {

	final int maxThr = 10; // number of threads to run

	// drawing area (must be xa<xb and ya<yb)
	BigDecimal xa = valueOf(-2.0);
	BigDecimal xb = valueOf(1.0);
	BigDecimal ya = valueOf(-1.5);
	BigDecimal yb = valueOf(1.5);

	private Image image = null;

	private final int maxIterations;

	private final int resolutionFactor;

	private final List<MandelbrotFractalThread> threads = new ArrayList<MandelbrotFractalThread>();

	public MandelbrotFractal(int maxIterations, int resolutionFactor) {
		this.maxIterations = maxIterations;
		this.resolutionFactor = resolutionFactor;
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
				redraw();
			}
		});
	}

	private void redraw() {
		paintFractal(getSize().width / 10, getSize().height / 10);
		repaint();
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		paintFractal(resolutionFactor * getSize().width, resolutionFactor
				* getSize().height);
		repaint();
	}

	private Image paintFractal(int w, int h) {
		int alpha = 255;
		int[] pix = new int[w * h];

		FractalMonitorThread monitor = paintFractal(maxThr, pix, w, h, xa, ya,
				xb, yb, alpha);
		try {
			monitor.join();
		} catch (InterruptedException e) {
			// do nothing
		}

		return createImage(new MemoryImageSource(w, h, pix, 0, w));
	}

	@Override
	public void paintComponent(Graphics g) {
		System.out.println("painting component " + getSize());
		if (image == null)
			redraw();
		else
			g.drawImage(image, 0, 0, getSize().width, getSize().height, this);
	}

	public FractalMonitorThread paintFractal(int numThreads, final int[] pix,
			final int w, final int h, BigDecimal xa, BigDecimal ya,
			BigDecimal xb, BigDecimal yb, int alpha) {

		long startTime = System.currentTimeMillis();
		List<MandelbrotFractalThread> threads = new ArrayList<MandelbrotFractalThread>();
		for (int i = 0; i < numThreads; i++) {
			MandelbrotFractalThread thread = new MandelbrotFractalThread(
					maxIterations, i, numThreads, pix, w, h, xa, ya, xb, yb,
					alpha);
			threads.add(thread);
			thread.start();
		}

		Runnable onFinish = new Runnable() {
			public void run() {
				image = createImage(new MemoryImageSource(w, h, pix, 0, w));
			}
		};
		FractalMonitorThread monitor = new FractalMonitorThread(startTime,
				threads, onFinish);
		monitor.start();
		return monitor;
	}

}
/** end of http://code.activestate.com/recipes/577158/ }}} */
