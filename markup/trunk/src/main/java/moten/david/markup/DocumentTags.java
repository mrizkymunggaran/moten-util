package moten.david.markup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentTags {

	private final List<DocumentTag<? extends Serializable>> list = new ArrayList<DocumentTag<? extends Serializable>>();
	private final Set<DocumentTag<? extends Serializable>> visible = new HashSet<DocumentTag<? extends Serializable>>();

	public List<DocumentTag<? extends Serializable>> getList() {
		return list;
	}

	public Set<DocumentTag<? extends Serializable>> getVisible() {
		return visible;
	}

}
