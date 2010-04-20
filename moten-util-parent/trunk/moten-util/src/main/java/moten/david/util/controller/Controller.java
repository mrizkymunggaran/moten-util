package moten.david.util.controller;

public interface Controller {
	void event(Event event);

	<T extends Event> void addListener(Class<T> cls,
			ControllerListener<T> listener);

	<T extends Event> boolean removeListener(Class<T> cls,
			ControllerListener<T> listener);

}
