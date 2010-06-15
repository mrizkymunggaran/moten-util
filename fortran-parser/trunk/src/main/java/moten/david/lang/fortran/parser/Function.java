package moten.david.lang.fortran.parser;

public class Function extends Node {

    public Function(Node parent, String name) {
        super(parent, "function " + name);
    }

}
