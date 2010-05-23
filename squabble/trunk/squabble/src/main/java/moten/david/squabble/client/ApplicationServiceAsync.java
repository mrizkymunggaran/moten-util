package moten.david.squabble.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {

    void getGame(AsyncCallback<String> callback);

    void getChat(AsyncCallback<String> callback);

    void submitWord(String user, String word, AsyncCallback<String> callback);

    void submitMessage(String user, String message,
            AsyncCallback<String> callback);

    void turnLetter(String user, AsyncCallback<Void> callback);
}
