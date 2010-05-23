package moten.david.squabble.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface ApplicationService extends RemoteService {

    String getGame();

    String getChat();

    String submitMessage(String user, String message);

    String submitWord(String user, String word);

    void turnLetter(String user);
}
