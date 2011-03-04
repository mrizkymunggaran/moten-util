package org.moten.david.util.xsd.simplified;

import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableMap;

public class ComplexType implements PartList, Type {

	private QName name;
	private ImmutableMap<String, QName> attributes;
	private List<Part> list;

	@Override
	public List<Part> getList() {
		return list;
	}

	@Override
	public QName getQName() {
		return name;
	}

}
