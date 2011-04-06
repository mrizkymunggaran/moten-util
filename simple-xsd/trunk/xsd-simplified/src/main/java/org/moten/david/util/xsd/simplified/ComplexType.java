package org.moten.david.util.xsd.simplified;

import java.util.List;
import java.util.Map;

public class ComplexType implements Type, Group {

	private QName name;
	private Map<String, QName> attributes;
	private Group group;

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setName(QName name) {
		this.name = name;
	}

	public void setAttributes(Map<String, QName> attributes) {
		this.attributes = attributes;
	}

	public QName getName() {
		return name;
	}

	public Map<String, QName> getAttributes() {
		return attributes;
	}

	public Group getGroup() {
		return group;
	}

	@Override
	public QName getQName() {
		return name;
	}

	public ComplexType() {

	}

	public ComplexType(QName name, Group group, Map<String, QName> attributes) {
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

	@Override
	public String toString() {
		return "\n\tComplexType [name=" + name + ", attributes=" + attributes
				+ ", \n\tgroup=" + group + "]";
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
