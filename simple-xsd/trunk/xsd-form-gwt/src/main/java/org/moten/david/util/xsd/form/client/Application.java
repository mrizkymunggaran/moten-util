package org.moten.david.util.xsd.form.client;

import org.moten.david.util.xsd.simplified.Schema;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private final Messages messages = GWT.create(Messages.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button sendButton1 = new Button(messages.sendButton() + " 1");
		// We can add style names to widgets
		sendButton1.addStyleName("sendButton");
		final Button sendButton2 = new Button(messages.sendButton() + " 2");
		// We can add style names to widgets
		sendButton2.addStyleName("sendButton");
		final Label errorLabel = new Label();

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		// RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton1);
		RootPanel.get("sendButtonContainer").add(sendButton2);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		sendButton1.setFocus(true);
		// nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton1.setEnabled(true);
				sendButton1.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler {

			private final String schema;

			public MyHandler(String schema) {
				this.schema = schema;
			}

			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				loadSchema(schema);
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void loadSchema(String schema) {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = "blah";
				// Disable the verifier for simple-xsd.

				// if (!FieldVerifier.isValidName(textToServer)) {
				// errorLabel.setText("Please enter at least four characters");
				// return;
				// }

				// Then, we send the input to the server.
				sendButton1.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.getSchema(schema, new AsyncCallback<Schema>() {

					public void onFailure(Throwable arg0) {
						// Show the RPC error message to the user
						dialogBox.setText("RPC - Failure");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(Schema result) {
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result.getNamespace());
						try {
							Panel schemaPanel = new SchemaPanel(result);
							RootPanel.get("schemaContainer").clear();
							RootPanel.get("schemaContainer").add(schemaPanel);
						} catch (RuntimeException e) {
							serverResponseLabel
									.addStyleName("serverResponseLabelError");
							serverResponseLabel.setHTML(e.getMessage());
						}
						sendButton1.setEnabled(true);
					}
				});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler1 = new MyHandler("/test.xsd");
		MyHandler handler2 = new MyHandler("/test-complex.xsd");
		sendButton1.addClickHandler(handler1);
		sendButton2.addClickHandler(handler2);
		sendButton1.click();
	}
}
