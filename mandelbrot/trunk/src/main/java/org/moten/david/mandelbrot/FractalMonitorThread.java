package org.moten.david.mandelbrot;

import java.util.List;

public class FractalMonitorThread extends Thread {

	private final List<MandelbrotFractalThread> threads;
	private final long startTime;
	private final Runnable onFinish;

	public FractalMonitorThread(long startTime,
			List<MandelbrotFractalThread> threads, Runnable onFinish) {
		super();
		this.startTime = startTime;
		this.threads = threads;
		this.onFinish = onFinish;
	}

	@Override
	public void run() {

		// wait until all threads finished
		boolean stop;
		do {
			stop = true;
			for (MandelbrotFractalThread thread : threads) {
				if (thread.isAlive()) {
					stop = false;
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// do nothing
			}
		} while (!stop);

		System.out.println("Number of threads: " + threads.size());
		long timeInMillis = System.currentTimeMillis() - startTime;
		System.out.println("Run Time in Millis: " + timeInMillis);
		onFinish.run();
	}
}
