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
        return "MyIdentifier [type=" + type + ", value=" + value + "]";
    }
}
