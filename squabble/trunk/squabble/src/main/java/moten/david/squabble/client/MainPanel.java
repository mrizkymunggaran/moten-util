package moten.david.squabble.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
    TextBox command;

    @UiField
    Button submit;

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        name.setText("Franco");
        game.setText("Board: a b g c\nDave: bard garam\nJane: cheerio harass");
        chat
                .setText("Jane submitted word ARCHER but it was rejected\nDave: shame Jane!");
        command.setText("");
    }

    @UiHandler("submit")
    void handleClick(ClickEvent e) {
        if (command.getText().startsWith("."))
            chat.setText(chat.getText() + "\n" + name.getText() + ": "
                    + command.getText().substring(1));
        chat.setCursorPos(chat.getText().length());
    }
}
