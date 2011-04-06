package org.moten.david.util.xsd.simplified;

import java.util.List;

public class Sequence implements Group {
	private Group group;

	public Sequence() {

	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Sequence(Group group) {
		this.group = group;
	}

	@Override
	public MaxOccurs getMaxOccurs() {
		return group.getMaxOccurs();
	}

	@Override
	public String toString() {
		return "Sequence [group=" + group + "]";
	}

	@Override
	public int getMinOccurs() {
		return group.getMinOccurs();
	}

	@Override
	public List<Particle> getParticles() {
		return group.getParticles();
	}

	@Override
	public void setMinOccurs(int minOccurs) {
		group.setMinOccurs(minOccurs);
	}

	@Override
	public void setParticles(List<Particle> particles) {
		group.setParticles(particles);
	}
}
