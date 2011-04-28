package org.moten.david.mandelbrot;

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

	MandelbrotFractalThread(int k, int maxThr, int[] pix, int w, int h,
			BigDecimal xa, BigDecimal ya, BigDecimal xb, BigDecimal yb,
			int alpha) {
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

			for (int kc = 0; kc < 256; kc++) {
				double x0 = x * x - y * y + a;
				// BigDecimal x0 = x.pow(2).subtract(y.pow(2)).add(a);
				y = 2 * x * y + b;
				// y = valueOf(2).multiply(x).multiply(y).add(b);
				x = x0;

				if (x * x + y * y > 4) {
					// various color palettes can be created here!
					int red = 255 - (kc % 16) * 16;
					int green = (16 - kc % 16) * 16;
					int blue = (kc % 16) * 16;

					pix[w * ky + kx] = (alpha << 24) | (red << 16)
							| (green << 8) | blue;
					break;
				}
			}
		}
	}
}
