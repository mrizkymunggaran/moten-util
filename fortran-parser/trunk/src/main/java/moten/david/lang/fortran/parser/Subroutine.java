package moten.david.lang.fortran.parser;

public class Subroutine extends Node {

    public Subroutine(Node parent, String name) {
        super(parent, "subroutine " + name);
    }

}
