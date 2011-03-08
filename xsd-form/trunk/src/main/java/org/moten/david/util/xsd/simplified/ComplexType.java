package org.moten.david.util.xsd.simplified;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableMap;

public class ComplexType extends BasicGroup implements Type {

	private QName name;
	private ImmutableMap<String, QName> attributes;

	@Override
	public QName getQName() {
		return name;
	}

}
