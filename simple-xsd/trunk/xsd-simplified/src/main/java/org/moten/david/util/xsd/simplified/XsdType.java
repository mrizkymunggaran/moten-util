package org.moten.david.util.xsd.simplified;

import java.io.Serializable;

public interface XsdType<T> extends Serializable {
	T getValue();

	QName getQName();

	void setValue(T t);

	void setQName(QName q);
}
