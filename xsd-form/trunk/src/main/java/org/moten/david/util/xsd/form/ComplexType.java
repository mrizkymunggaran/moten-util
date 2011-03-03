package org.moten.david.util.xsd.form;

import java.util.List;
import java.util.Map;

public class ComplexType implements PartList, Type {

	private String name;
	private Map<String, SimpleType<?>> attributes;
	private List<Part> list;

	public List<Part> getList() {
		return list;
	}

	public String getName() {
		return name;
	}

}
