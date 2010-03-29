package moten.david.ete;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

public interface Fix {
    Set<Identifier> getIdentifiers();

    Position getPosition();

    Calendar getTime();

    Source getSource();

    Map<String, String> getProperties();

    void removeIdentifier(Identifier id);

    EntityType getType();
}
