package org.moten.david.physics.fluids;

import java.util.ArrayList;
import java.util.List;

public class Run {

	private final Data data;
	private final double x1;
	private final double x2;
	private final double y1;
	private final double y2;
	private final double maxDepth;
	private final double timeStep;
	private final long numSteps;

	public Run(Data data, double x1, double y1, double x2, double y2,
			double maxDepth, double timeStep, long numSteps) {
		this.data = data;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.maxDepth = maxDepth;
		this.timeStep = timeStep;
		this.numSteps = numSteps;
	}

	private final List<RunListener> listeners = new ArrayList<RunListener>();

	public void start() {
		for (int i = 0; i < numSteps; i++) {
			NavierStokesSolver solver = new NavierStokesSolver();
			fireFinishedStep(data);
		}

	}

	private void fireFinishedStep(Data data) {
		for (RunListener l : listeners)
			l.stepFinished();
	}

}
