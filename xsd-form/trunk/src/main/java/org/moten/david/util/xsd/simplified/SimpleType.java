package org.moten.david.util.xsd.simplified;

import javax.xml.namespace.QName;

public class SimpleType implements Particle, Type {

	private final QName name;
	private final Restriction restriction;

	public SimpleType(QName name, Restriction restriction) {
		this.name = name;
		this.restriction = restriction;
	}

	public SimpleType(QName name) {
		this(name, null);
	}

	@Override
	public QName getQName() {
		return name;
	}

}
