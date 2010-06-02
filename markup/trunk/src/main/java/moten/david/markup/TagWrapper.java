package moten.david.markup;

import moten.david.markup.xml.study.Tag;

/**
 * Provides a toString method with the tag name.
 * 
 * @author dave
 * 
 */
public class TagWrapper {

	private final Tag tag;

	public TagWrapper(Tag tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Integer tagId : tag.getMemberOf()) {
			if (s.length() > 0)
				s.append(",");
			s.append(tagId);
		}
		if (s.length() > 0) {
			s.insert(0, "(");
			s.append(")");
		}
		return tag.getId() + ". " + tag.getName() + " " + s;
	}

	public Tag getTag() {
		return tag;
	}

}
