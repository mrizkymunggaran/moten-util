package moten.david.ete.memory;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import moten.david.ete.Entity;
import moten.david.ete.Identifier;
import moten.david.ete.memory.event.IdentifierAdded;
import moten.david.ete.memory.event.IdentifierRemoved;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;

import com.google.inject.Inject;

public class EntityLookupMap implements EntityLookup {

	private final Map<Identifier, Entity> identifiers = new ConcurrentHashMap<Identifier, Entity>();

	@Inject
	public EntityLookupMap(Controller controller) {
		controller.addListener(IdentifierAdded.class,
				new ControllerListener<IdentifierAdded>() {
					@Override
					public void event(IdentifierAdded event) {
						identifiers.put(event.getIdentifier(), event
								.getEntity());
					}
				});
		controller.addListener(IdentifierRemoved.class,
				new ControllerListener<IdentifierRemoved>() {
					@Override
					public void event(IdentifierRemoved event) {
						identifiers.remove(event.getIdentifier());
					}
				});
	}

	@Override
	public Entity findEntity(SortedSet<? extends Identifier> identifiers) {
		for (Identifier id : identifiers) {
			Entity entity = this.identifiers.get(id);
			if (entity != null)
				return entity;
		}
		return null;
	}

}
