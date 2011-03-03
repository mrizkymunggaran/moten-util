package org.moten.david.util.xsd.form;

public class Element implements Part {
	private XsdString name;
	private final int minOccurs = 1;
	private final int maxOccurs = 1;
	private final boolean maxUnbounded = false;
	private Type type;
}
