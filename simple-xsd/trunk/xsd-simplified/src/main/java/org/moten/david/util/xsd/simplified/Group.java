package org.moten.david.util.xsd.simplified;

import java.util.List;

public interface Group extends Particle {
	MaxOccurs getMaxOccurs();

	int getMinOccurs();

	void setMinOccurs(int minOccurs);

	List<Particle> getParticles();

	void setParticles(List<Particle> particles);
}