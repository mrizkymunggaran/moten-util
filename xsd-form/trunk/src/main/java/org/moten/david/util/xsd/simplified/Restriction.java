package org.moten.david.util.xsd.simplified;

import java.util.List;

public class Restriction<T extends XsdType<?>> {
	private List<T> enumerations;
	private String pattern;

}
