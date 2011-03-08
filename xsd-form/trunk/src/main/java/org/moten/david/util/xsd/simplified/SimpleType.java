package org.moten.david.util.xsd.simplified;

import javax.xml.namespace.QName;

public class SimpleType<T extends XsdType> implements Part, Type {

	private final QName name;
	private Restriction<T> restriction;

	public SimpleType(QName name, Restriction<T> restriction) {
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
