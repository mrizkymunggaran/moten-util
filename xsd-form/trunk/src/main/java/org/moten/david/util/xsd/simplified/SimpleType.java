package org.moten.david.util.xsd.simplified;

import javax.xml.namespace.QName;

public class SimpleType<T extends XsdType> implements Part, Type {

	private QName name;
	private Restriction<T> restriction;

	@Override
	public QName getQName() {
		return name;
	}

}
