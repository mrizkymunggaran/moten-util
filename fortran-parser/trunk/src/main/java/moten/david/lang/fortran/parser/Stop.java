package moten.david.lang.fortran.parser;

public class Stop extends Node {

    public Stop(Node parent, String name) {
        super(parent, name);
    }

    @Override
    public String toString() {
        return "stop " + (getName() == null ? "" : getName());
    }
}
