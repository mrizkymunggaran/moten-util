package moten.david.squabble.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {
    private static final String COOKIE_SQUABBLE_NAME = "squabble.name";

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

    @UiField
    Button turnLetter;

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final ApplicationServiceAsync applicationService = GWT
            .create(ApplicationService.class);
    private final AsyncCallback<String> submitMessageCallback;
    private final AsyncCallback<String> getChatCallback;
    private final AsyncCallback<String> getGameCallback;

    private final AsyncCallback<String> submitWordCallback;
    private final AsyncCallback<Void> turnLetterCallback;

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        command.setText("");
        submitMessageCallback = createSubmitMessageCallback();
        getChatCallback = createGetChatCallback();
        submitWordCallback = createSubmitWordCallback();
        getGameCallback = createGetGameCallback();
        turnLetterCallback = createTurnLetterCallback();
        command.addKeyPressHandler(createCommandKeyPressHandler());
        turnLetter.addClickHandler(createTurnLetterClickHandler());
        createTimer();
        command.setFocus(true);
        String nameCookie = Cookies.getCookie(COOKIE_SQUABBLE_NAME);
        if (nameCookie != null && nameCookie.trim().length() > 0)
            name.setText(nameCookie);
        name.addChangeHandler(createNameChangeHandler());
    }

    private ChangeHandler createNameChangeHandler() {
        return new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Cookies.removeCookie(COOKIE_SQUABBLE_NAME);
                Cookies.setCookie(COOKIE_SQUABBLE_NAME, name.getName(),
                        new Date(System.currentTimeMillis() + 60 * 24 * 60 * 60
                                * 1000));
                command.setEnabled(true);
                command.setText("");
                turnLetter.setEnabled(true);
                submit.setEnabled(true);
            }
        };
    }

    private AsyncCallback<Void> createTurnLetterCallback() {
        return new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable t) {
                reportError(t);
            }

            @Override
            public void onSuccess(Void v) {
                command.setText("");
                command.setFocus(true);
                turnLetter.setEnabled(false);
                Timer timer = new Timer() {
                    @Override
                    public void run() {
                        turnLetter.setEnabled(true);
                    }
                };
                timer.schedule(2000);
            }
        };
    }

    private ClickHandler createTurnLetterClickHandler() {
        return new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                applicationService.turnLetter(name.getText(),
                        turnLetterCallback);
            }
        };
    }

    private Timer createTimer() {
        Timer timer = new Timer() {

            @Override
            public void run() {
                applicationService.getGame(getGameCallback);
                applicationService.getChat(getChatCallback);
            }
        };
        timer.scheduleRepeating(1000);
        timer.run();
        return timer;
    }

    private KeyPressHandler createCommandKeyPressHandler() {
        return new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
                    submit.click();
                    command.setText("");
                }
            }
        };
    }

    private AsyncCallback<String> createGetGameCallback() {
        return new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable t) {
                reportError(t);
            }

            @Override
            public void onSuccess(String gameLines) {
                game.setText(gameLines);
            }
        };

    }

    private AsyncCallback<String> createSubmitWordCallback() {
        return new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                reportError(t);
                submit.setEnabled(true);
            }

            public void onSuccess(String result) {
                command.setText("");
                command.setFocus(true);
                submit.setEnabled(true);
                if (!result.equals(null))
                    turnLetter.setEnabled(true);
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
        if (command.getText().trim().equals(""))
            turnLetter.click();
        else if (command.getText().startsWith(" "))
            applicationService.submitMessage(name.getText(), command.getText()
                    .substring(1), submitMessageCallback);
        else
            applicationService.submitWord(name.getText(), command.getText(),
                    submitWordCallback);
    }

}
