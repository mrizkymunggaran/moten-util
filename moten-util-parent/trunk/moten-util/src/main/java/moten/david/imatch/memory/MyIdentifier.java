package moten.david.imatch.memory;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierType;

public class MyIdentifier implements Identifier {

    private final MyIdentifierType type;
    private final String value;

    public MyIdentifier(MyIdentifierType type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    @Override
    public IdentifierType getIdentifierType() {
        return type;
    }

    @Override
    public String toString() {
        return "[" + type + ":" + value + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        MyIdentifier other = (MyIdentifier) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
