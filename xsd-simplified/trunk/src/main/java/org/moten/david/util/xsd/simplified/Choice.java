package org.moten.david.util.xsd.simplified;

import java.util.List;

public class Choice implements Group {
	private final Group group;

	public Choice(Group group) {
		this.group = group;
	}

	@Override
	public MaxOccurs getMaxOccurs() {
		return group.getMaxOccurs();
	}

	@Override
	public String toString() {
		return "Choice [group=" + group + "]";
	}

	@Override
	public int getMinOccurs() {
		return group.getMinOccurs();
	}

	@Override
	public List<Particle> getParticles() {
		return group.getParticles();
	}
}
