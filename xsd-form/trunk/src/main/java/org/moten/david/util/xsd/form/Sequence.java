package org.moten.david.util.xsd.form;

import java.util.List;

public class Sequence implements Part, PartList {

	private List<Part> list;

	public List<Part> getList() {
		return list;
	}

}
