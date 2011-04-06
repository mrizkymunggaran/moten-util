package org.moten.david.util.xsd.simplified;

import java.io.Serializable;

public class QName implements Serializable {

	private String namespace;
	private String element;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getElement() {
		return element;
	}

	@Override
	public String toString() {
		return "QName [namespace=" + namespace + ", element=" + element + "]";
	}

	public void setElement(String element) {
		this.element = element;
	}

	public QName() {

	}

	public QName(String namespace, String element) {
		this.namespace = namespace;
		this.element = element;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result
				+ ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QName other = (QName) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

}
