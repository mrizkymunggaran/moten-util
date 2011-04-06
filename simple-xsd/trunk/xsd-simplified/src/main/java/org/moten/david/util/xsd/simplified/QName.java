package org.moten.david.util.xsd.simplified;

import java.io.Serializable;

public class QName implements Serializable {

	private String namespace;
	private String localPart;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getLocalPart() {
		return localPart;
	}

	@Override
	public String toString() {
		return "QName [namespace=" + namespace + ", element=" + localPart + "]";
	}

	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}

	public QName() {

	}

	public QName(String namespace, String localPart) {
		this.namespace = namespace;
		this.localPart = localPart;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localPart == null) ? 0 : localPart.hashCode());
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
		if (localPart == null) {
			if (other.localPart != null)
				return false;
		} else if (!localPart.equals(other.localPart))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

}
