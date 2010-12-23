package moten.david.util.monitoring.gwt.client.controller;


public interface ControllerListener<T extends Event> {
	void event(T event);
}
