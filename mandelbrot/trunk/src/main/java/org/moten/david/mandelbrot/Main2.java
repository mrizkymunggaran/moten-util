package org.moten.david.mandelbrot;

import static java.awt.Color.black;
import static java.awt.Color.blue;
import static java.awt.Color.green;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.awt.Color.yellow;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Main2 {
	public static void main(String[] args) throws IOException {
		int h = 700;
		int w = h;
		int numFrames = 100;
		BigDecimal startxa = d("-3.5");
		BigDecimal startya = d("-2.5");
		BigDecimal startxb = d("1.5");
		BigDecimal startyb = d("2.5");
		// (-0.4700725,-0.6217725)-(-0.4700675,-0.6217675)
		// BigDecimal endxa = d("-1.740062382579339905");
		// BigDecimal endya = d("-0.028175339779211048");
		// BigDecimal endxb = d("-1.740062382579339906");
		// BigDecimal endyb = d("-0.028175339779211048");
		BigDecimal endxa = d("-0.4700");
		BigDecimal endya = d("-0.6210");
		BigDecimal endxb = d("-0.4701");
		BigDecimal endyb = d("-0.6211");
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
		// (-0.718250000,-0.286750000)-(-0.713250000,-0.281750000)

	}

	private static void paintImage(int maxIterations, int w, int h,
			BigDecimal xa, BigDecimal ya, BigDecimal xb, BigDecimal yb,
			int imageNo) {
		DecimalFormat df = new DecimalFormat("000000");
		BufferedImage image = MandelbrotFractal.paintFractal(Runtime
				.getRuntime().availableProcessors(), 4096, w, h, xa, ya, xb,
				yb, getColors());
		try {
			ImageIO.write(image, "png",
					new File("target/p" + df.format(imageNo) + ".png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<Color> getColors() {
		return new ArrayList<Color>() {
			{
				add(blue);
				add(white);
				add(yellow);
				add(black);
				add(green);
				add(white);
				add(red);
				add(black);
			}
		};
	}

	private static BigDecimal d(String s) {
		return new BigDecimal(s);
	}
}
