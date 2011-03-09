package org.moten.david.util.xsd.simplified;

import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableMap;

public class ComplexType implements Type, Group {

	private final QName name;
	private final ImmutableMap<String, QName> attributes;
	private final Group group;

	@Override
	public QName getQName() {
		return name;
	}

	public ComplexType(QName name, Group group,
			ImmutableMap<String, QName> attributes) {
		this.name = name;
		this.group = group;
		this.attributes = attributes;
	}

	@Override
	public MaxOccurs getMaxOccurs() {
		return group.getMaxOccurs();
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
