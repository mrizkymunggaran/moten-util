package org.moten.david.util.xsd.form;

import javax.xml.namespace.QName;

public interface XsdType<T> {
	T getValue();

	QName getQName();
}
