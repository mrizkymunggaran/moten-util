package moten.david.markup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentTags {

    private final List<DocumentTag<? extends Serializable>> list = new ArrayList<DocumentTag<? extends Serializable>>();
    private final Set<Tag> visible = new HashSet<Tag>();

    public List<DocumentTag<? extends Serializable>> getList() {
        return list;
    }

    public Set<Tag> getVisible() {
        return visible;
    }

    public boolean isVisible(DocumentTag documentTag) {
        return visible.contains(documentTag.getTag());
    }

}
