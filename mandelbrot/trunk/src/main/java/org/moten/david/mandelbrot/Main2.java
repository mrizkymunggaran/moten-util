package org.moten.david.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

public class Main2 {
	public static void main(String[] args) throws IOException {
		int h = 240;
		int w = 340;
		int numFrames = 260;
		BigDecimal startxa = d("-3.5");
		BigDecimal startya = d("-2.5");
		BigDecimal startxb = d("1.5");
		BigDecimal startyb = d("2.5");
		// (-0.4700725,-0.6217725,-0.4700675,-0.6217675)
		// (-0.7621019625,-0.0880306125,-0.7621019575,-0.0880306075)
		// String s = "-0.7621019625,-0.0880306125,-0.7621019575,-0.0880306075";
		String s = "-0.0104807504724003896025,-0.6488002794502981524025,-0.0104807504724003895975,-0.6488002794502981523975";
		if (args.length > 0)
			s = args[0];
		String[] arr = s.split(",");
		BigDecimal endxa = d(arr[0]);
		BigDecimal endya = d(arr[1]);
		BigDecimal endxb = d(arr[2]);
		BigDecimal endyb = d(arr[3]);
		BigDecimal endSizeX = endxb.subtract(endxa);
		BigDecimal endSizeY = endyb.subtract(endya);
		// paintImage(4096, w, h, endxa, endya, endxb, endyb, 0);
		BigDecimal xa = startxa;
		BigDecimal ya = startya;
		BigDecimal xb = startxb;
		BigDecimal yb = startyb;
		BigDecimal change = BigDecimal.valueOf(0.9);
		for (int i = 1; i <= numFrames; i++) {
			System.out.println("(" + xa + "," + ya + ")-(" + xb + "," + yb
					+ ")");
			paintImage(4096, w, h, xa, ya, xb, yb, i);
			System.out.println("writing image " + i);
			BigDecimal sizeX = xb.subtract(xa);
			BigDecimal sizeY = yb.subtract(ya);

			xa = xa.subtract(endxa).multiply(change).add(endxa);
			ya = ya.subtract(endya).multiply(change).add(endya);
			xb = xa.add(endSizeX.add(sizeX.subtract(endSizeX).multiply(change)));
			yb = ya.add(endSizeY.add(sizeY.subtract(endSizeY).multiply(change)));

		}
	}

	private static void paintImage(int maxIterations, int w, int h,
			BigDecimal xa, BigDecimal ya, BigDecimal xb, BigDecimal yb,
			int imageNo) {
		DecimalFormat df = new DecimalFormat("000000");
		BufferedImage image = MandelbrotFractal.paintFractal(Runtime
				.getRuntime().availableProcessors(), 4096, w, h, xa, ya, xb,
				yb, Scheme.wikipedia);
		try {
			ImageIO.write(image, "png",
					new File("target/p" + df.format(imageNo) + ".png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static BigDecimal d(String s) {
		return new BigDecimal(s);
	}
}
