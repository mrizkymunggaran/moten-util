package org.moten.david.util.xsd.form;

import java.util.List;

public class Restriction<T extends XsdType<?>> {
	private List<T> enumerations;
	private String pattern;

}
