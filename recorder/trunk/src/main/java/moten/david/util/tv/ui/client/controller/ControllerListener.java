package moten.david.util.tv.ui.client.controller;


public interface ControllerListener<T extends Event> {
	void event(T event);
}
