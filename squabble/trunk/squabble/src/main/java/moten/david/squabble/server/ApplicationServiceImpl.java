package moten.david.squabble.server;

import java.util.ArrayList;
import java.util.List;

import moten.david.squabble.Data;
import moten.david.squabble.DictionaryAlwaysValid;
import moten.david.squabble.Engine;
import moten.david.squabble.Letters;
import moten.david.squabble.Service;
import moten.david.squabble.client.ApplicationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
        ApplicationService {

    private static final long serialVersionUID = 8564374631589515374L;

    private final List<String> chat = new ArrayList<String>();

    private final Service service = new Service(new Engine(
            new DictionaryAlwaysValid(), new Letters("eng")));

    @Override
    public String getChat() {
        int start = Math.max(0, chat.size() - 10);
        StringBuffer s = new StringBuffer();
        for (int i = start; i < chat.size(); i++) {
            if (s.length() > 0)
                s.append("\n");
            s.append(chat.get(i));
        }
        return s.toString();
    }

    @Override
    public String getGame() {
        Data data = service.getData();
        return data.toString();
    }

    @Override
    public synchronized String submitWord(String user, String words) {
        String[] items = words.split(" ");
        String result = "";
        for (String word : items) {
            result = submitChatLine(user + " submitted " + word);
            service.addWord(user, word);
        }
        service.turnLetter();
        return result;
    }

    private String submitChatLine(String message) {
        chat.add(message);
        System.out.println(message);
        return getChat();
    }

    @Override
    public String submitMessage(String user, String message) {
        return submitChatLine(user + ": " + message);
    }

}