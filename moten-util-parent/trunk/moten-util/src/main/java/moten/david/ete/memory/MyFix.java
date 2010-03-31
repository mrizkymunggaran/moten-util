package moten.david.ete.memory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import moten.david.ete.AbstractFix;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;
import moten.david.ete.Position;

public class MyFix extends AbstractFix {

    private final Set<Identifier> identifiers = new HashSet<Identifier>();
    private final Position position;

    public MyFix(Position position, Calendar time) {
        this.position = position;
        this.time = time;
    }

    private final Calendar time;
    private final Map<String, String> properties = new HashMap<String, String>();

    @Override
    public Set<Identifier> getIdentifiers() {
        return identifiers;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public Calendar getTime() {
        return time;
    }

    @Override
    public int compareTo(Fix o) {
        return getTime().compareTo(o.getTime());
    }

}
