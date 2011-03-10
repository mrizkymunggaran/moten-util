package org.moten.david.util.xsd.simplified;

import java.util.List;

public interface Group extends Particle {
	MaxOccurs getMaxOccurs();

	int getMinOccurs();

	List<Particle> getParticles();
}