package moten.david.lang.fortran.parser;

public class Variable {

    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
