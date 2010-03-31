package moten.david.ete.memory;

import moten.david.ete.IdentifierType;

public class MyIdentifierType implements IdentifierType {

    private final String name;
    private final double strength;

    public MyIdentifierType(String name, double strength) {
        super();
        this.name = name;
        this.strength = strength;
    }

    @Override
    public int compareTo(IdentifierType o) {
        return Double.compare(strength, ((MyIdentifierType) o).strength);
    }

    @Override
    public String toString() {
        return "MyIdentifierType [name=" + name + ", strength=" + strength
                + "]";
    }

}
