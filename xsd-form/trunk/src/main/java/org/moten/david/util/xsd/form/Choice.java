package org.moten.david.util.xsd.form;

import java.util.List;

public class Choice implements Part, PartList {

	private List<Part> list;

	@Override
	public List<Part> getList() {
		return list;
	}

}
