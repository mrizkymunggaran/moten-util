package moten.david.ete.memory;

import moten.david.ete.Identifier;
import moten.david.ete.IdentifierType;

public class MyIdentifier implements Identifier {

    private final IdentifierType type;
    private final String value;

    public MyIdentifier(IdentifierType type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    @Override
    public IdentifierType getIdentifierType() {
        return type;
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

    @Override
    public int compareTo(Identifier o) {
        // TODO write unit tests to test all required qualities for the
        // comparison.
        if (this.getIdentifierType().equals(o.getIdentifierType()))
            return this.value.compareTo(((MyIdentifier) o).value);
        else
            return this.getIdentifierType().compareTo(o.getIdentifierType());
    }
}
