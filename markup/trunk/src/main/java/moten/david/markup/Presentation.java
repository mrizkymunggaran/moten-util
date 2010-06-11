package moten.david.markup;

import java.util.HashMap;

public class Presentation {
    public SelectionMode selectionMode = SelectionMode.SENTENCE;
    public final HashMap<Integer, Boolean> visible = new HashMap<Integer, Boolean>();
    public final HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();
}
