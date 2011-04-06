package org.moten.david.util.xsd.simplified;

public class SimpleType implements Particle, Type {

	private QName name;
	private Restriction restriction;

	public SimpleType() {

	}

	public QName getName() {
		return name;
	}

	public void setName(QName name) {
		this.name = name;
	}

	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;
	}

	public Restriction getRestriction() {
		return restriction;
	}

	public SimpleType(QName name, Restriction restriction) {
		this.name = name;
		this.restriction = restriction;
	}

	public SimpleType(QName name) {
		this(name, null);
	}

	@Override
	public String toString() {
		return "\n\tSimpleType [name=" + name + ", restriction=" + restriction
				+ "]";
	}

	@Override
	public QName getQName() {
		return name;
	}

}
