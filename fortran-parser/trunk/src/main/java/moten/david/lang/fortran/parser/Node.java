package moten.david.lang.fortran.parser;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final String name;

    private String label;

    private final Node parent;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public Node(Node parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return name;
    }

    private final List<Node> children = new ArrayList<Node>();

    public List<Node> getChildren() {
        return children;
    }

    public Node add(Node node) {
        children.add(node);
        return this;
    }
}
