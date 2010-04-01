package moten.david.ete.memory;

import moten.david.ete.Fix;
import moten.david.ete.Identifier;

public interface EntityListener {
	void fixAdded(MyEntity entity, Fix fix);

	void identifierAdded(MyEntity entity, Identifier identifier);

	void identifierRemoved(MyEntity entity, Identifier identifier);

}
