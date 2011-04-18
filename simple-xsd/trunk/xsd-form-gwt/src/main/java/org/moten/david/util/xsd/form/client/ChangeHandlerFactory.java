package org.moten.david.util.xsd.form.client;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.Label;

public interface ChangeHandlerFactory {
	ChangeHandler create(HasChangeHandlers item, Label validation);
}
