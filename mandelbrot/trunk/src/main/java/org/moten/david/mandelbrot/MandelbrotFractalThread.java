package org.moten.david.mandelbrot;

import java.awt.Color;
import java.math.BigDecimal;

public class MandelbrotFractalThread extends Thread {
	int k; // id number of this thread
	private final int maxThr;
	private final int[] pix;
	private final int w;
	private final int h;
	private final BigDecimal xa;
	private final BigDecimal ya;
	private final BigDecimal xb;
	private final BigDecimal yb;
	private final int alpha;
	private final int maxIterations = 1024;

	MandelbrotFractalThread(int maxIterations, int k, int maxThr, int[] pix,
			int w, int h, BigDecimal xa, BigDecimal ya, BigDecimal xb,
			BigDecimal yb, int alpha) {
		this.k = k;
		this.maxThr = maxThr;
		this.pix = pix;
		this.w = w;
		this.h = h;
		this.xa = xa;
		this.ya = ya;
		this.xb = xb;
		this.yb = yb;
		this.alpha = alpha;
	}

	@Override
	public void run() {
		int imax = w * h;

		int[] palette = new int[maxIterations];
		for (int j = 0; j < palette.length; j++)
			palette[j] = getColor(j);

		double diffX = xb.subtract(xa).doubleValue();
		double diffY = yb.subtract(ya).doubleValue();
		double xb = this.xb.doubleValue();
		double xa = this.xa.doubleValue();
		double ya = this.ya.doubleValue();
		double yb = this.yb.doubleValue();
		// Each thread only calculates its own share of pixels!
		for (int i = k; i < imax; i += maxThr) {
			int kx = i % w;
			int ky = (i - kx) / w;
			double a = (double) kx / w * (xb - xa) + xa;
			// BigDecimal a = valueOf(kx)
			// .divide(valueOf(w), SCALE, RoundingMode.HALF_UP)
			// .multiply(diffX).add(xa);
			double b = (double) ky / h * (yb - ya) + ya;
			// BigDecimal b = valueOf(ky)
			// .divide(valueOf(h), SCALE, RoundingMode.HALF_UP)
			// .multiply(diffY).add(ya);
			double x = a;
			double y = b;
			int v = 0;
			pix[w * ky + kx] = (alpha << 24) | (v << 16) | (v << 8) | v;

			for (int kc = 0; kc < maxIterations; kc++) {
				double x2 = x * x;
				double y2 = y * y;
				double x0 = x2 - y2 + a;
				// BigDecimal x0 = x.pow(2).subtract(y.pow(2)).add(a);
				y = 2 * x * y + b;
				// y = valueOf(2).multiply(x).multiply(y).add(b);
				x = x0;

				if (x2 + y2 > 4) {
					pix[w * ky + kx] = getColor(kc, x2 + y2);
					break;
				}
			}
		}
	}

	/**
	 * From http://linas.org/art-gallery/escape/escape.html, corrected using
	 * http://en.wikipedia.org/wiki/Mandelbrot_set
	 * 
	 * @param iteration
	 * @param x
	 * @param y
	 * @return
	 */
	private int getColor(int iteration, double zModulus) {
		float v = (float) (iteration - Math.log(Math.log(zModulus)
				/ Math.log(4))
				/ Math.log(2));
		if (v < 0)
			v = 0;
		float p = v / maxIterations;
		float red = 0;
		float green = 0;
		float blue = 0;
		float step = 0.25f;
		int numSteps = 4;
		if (p < step) {
			// blue to white
			blue = 1;
			red = green = p * numSteps;
		} else if (p < 2 * step) {
			// white to yellow
			blue = 1f;

			green = red = 1f - (p - step) * numSteps;
		} else if (p < 3 * step) {
			// yellow to black
			red = green = 1 - (p - 2 * step) * numSteps;
		} else {
			// black to blue
			blue = (p - 3 * step) * numSteps;
		}

		return new Color(red, green, blue).getRGB();
	}

	private int getColor(int iteration) {
		// various color palettes can be created here!
		int red = 255 - (iteration % 16) * 16;
		int green = (16 - iteration % 16) * 16;
		int blue = (iteration % 16) * 16;

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	private int getColor2(int iteration) {
		// various color palettes can be created here!
		final int MAX_COLOR = 255;
		int red;
		int green = 0;
		int blue = 0;
		if (iteration < maxIterations / 2) {
			red = 2 * iteration * MAX_COLOR / maxIterations;
		} else {
			iteration -= maxIterations / 2;
			red = 255;
			green = blue = 2 * iteration * MAX_COLOR / maxIterations;
		}
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
}
