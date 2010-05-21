package moten.david.squabble.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {
    interface MyUiBinder extends UiBinder<Widget, MainPanel> {
    };

    @UiField
    TextBox name;

    @UiField
    TextArea game;

    @UiField
    TextArea chat;

    @UiField
    TextArea command;

    @UiField
    Button submit;

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final ApplicationServiceAsync applicationService = GWT
            .create(ApplicationService.class);
    private final AsyncCallback<String> submitMessageCallback;
    private final AsyncCallback<String> getChatCallback;

    private final AsyncCallback<String> submitWordCallback;

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        name.setText("Franco");
        game.setText("Board: a b g c\nDave: bard garam\nJane: cheerio harass");
        chat
                .setText("Welcome to Squabble! Enter a word in the command box whenever you want.");
        command.setText("");
        submitMessageCallback = createSubmitMessageCallback();
        getChatCallback = createGetChatCallback();
        submitWordCallback = createSubmitWordCallback();
        command.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
                    submit.click();
            }
        });
    }

    private AsyncCallback<String> createSubmitWordCallback() {
        return new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                reportError(t);
            }

            public void onSuccess(String chatLines) {
                chat.setText(chatLines);
                command.setText("");
                command.setFocus(true);
            }
        };
    }

    private void reportError(Throwable t) {
        chat.setText(chat.getText() + "\n" + t.getMessage());
    }

    private AsyncCallback<String> createGetChatCallback() {
        return new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                reportError(t);
            }

            public void onSuccess(String chatLines) {
                chat.setText(chatLines);
                command.setText("");
                command.setFocus(true);
            }
        };
    }

    private AsyncCallback<String> createSubmitMessageCallback() {
        return new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                reportError(t);
            }

            public void onSuccess(String chatLines) {
                chat.setText(chatLines);
                command.setText("");
                command.setFocus(true);
            }
        };
    }

    @UiHandler("submit")
    void handleClick(ClickEvent e) {
        if (command.getText().startsWith("."))
            applicationService.submitMessage(name.getText(), command.getText()
                    .substring(1), submitMessageCallback);
        else
            applicationService.submitWord(name.getText(), command.getText(),
                    submitWordCallback);
    }
}
