package org.moten.david.physics.fluids;

import java.util.ArrayList;
import java.util.List;

public class Run {

	private final Data data;
	private final double timeStep;
	private final long numSteps;

	public Run(Data data, double timeStep, long numSteps, RunListener listener) {
		this.data = data;
		this.timeStep = timeStep;
		this.numSteps = numSteps;
		if (listener != null)
			listeners.add(listener);
	}

	private final List<RunListener> listeners = new ArrayList<RunListener>();

	public void start() {
		NavierStokesSolver solver = new NavierStokesSolver();
		Data data = this.data;
		for (int i = 0; i < numSteps; i++) {
			data = solver.getDataAfterTime(data, timeStep);
			fireFinishedStep(data);
		}
	}

	private void fireFinishedStep(Data data) {
		for (RunListener l : listeners)
			l.stepFinished(data);
	}

}
