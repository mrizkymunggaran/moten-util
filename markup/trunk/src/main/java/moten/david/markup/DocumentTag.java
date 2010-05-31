package moten.david.markup;

import java.io.Serializable;

public class DocumentTag<T extends Serializable> {
    private Tag<T> tag;
    private int start;
    private int length;
    private final T value;

    public Tag<T> getTag() {
        return tag;
    }

    public void setTag(Tag<T> tag) {
        this.tag = tag;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + start;
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocumentTag other = (DocumentTag) obj;
        if (length != other.length)
            return false;
        if (start != other.start)
            return false;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        return true;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public DocumentTag(Tag<T> tag, int start, int finish, Object value) {
        super();
        this.tag = tag;
        this.start = start;
        this.length = finish;
        this.value = (T) value;
    }

    public T getValue() {
        return value;
    }

}
