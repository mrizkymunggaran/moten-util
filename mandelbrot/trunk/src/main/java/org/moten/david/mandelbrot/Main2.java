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
		int numFrames = 1000;
		BigDecimal startxa = d("-3.5");
		BigDecimal startya = d("-2.5");
		BigDecimal startxb = d("1.5");
		BigDecimal startyb = d("2.5");
		// (-0.4700725,-0.6217725)-(-0.4700675,-0.6217675)
		// BigDecimal endxa = d("-1.740062382579339905");
		// BigDecimal endya = d("-0.028175339779211048");
		// BigDecimal endxb = d("-1.740062382579339906");
		// BigDecimal endyb = d("-0.028175339779211048");
		BigDecimal endxa = d("-4.4700725");
		BigDecimal endya = d("-0.6217725");
		BigDecimal endxb = d("-0.4700675");
		BigDecimal endyb = d("-0.6217675");
		paintImage(4096, w, h, endxa, endya, endxb, endyb, 0);
		BigDecimal xa = startxa;
		BigDecimal ya = startya;
		BigDecimal xb = startxb;
		BigDecimal yb = startyb;
		BigDecimal change = BigDecimal.valueOf(0.1);
		for (int i = 1; i <= numFrames; i++) {
			System.out.println("writing image " + i);
			xa = endxa.subtract(xa).multiply(change).add(xa);
			ya = endya.subtract(ya).multiply(change).add(ya);
			xb = endxb.subtract(xb).multiply(change).add(xb);
			yb = endyb.subtract(yb).multiply(change).add(yb);
			paintImage(4096, w, h, xa, ya, xb, yb, i);
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
