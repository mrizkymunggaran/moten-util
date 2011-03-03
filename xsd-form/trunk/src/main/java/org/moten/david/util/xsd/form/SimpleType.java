package org.moten.david.util.xsd.form;

public class SimpleType<T extends XsdType> implements Part, Type {

	private String name;
	private Restriction<T> restriction;

	public String getName() {
		return name;
	}

}
