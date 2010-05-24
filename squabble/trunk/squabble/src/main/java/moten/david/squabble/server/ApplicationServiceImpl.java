package moten.david.squabble.server;

import java.util.ArrayList;
import java.util.List;

import moten.david.squabble.Data;
import moten.david.squabble.Engine;
import moten.david.squabble.Letters;
import moten.david.squabble.Service;
import moten.david.squabble.Engine.WordStatus;
import moten.david.squabble.client.ApplicationService;
import moten.david.util.words.DictionarySowpods;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
        ApplicationService {

    private static final long serialVersionUID = 8564374631589515374L;

    private final List<String> chat = new ArrayList<String>();

    private Service service;

    public ApplicationServiceImpl() {
        service = createService();
    }

    private Service createService() {
        Service service = new Service(new Engine(new DictionarySowpods(),
                new Letters("eng")));
        service.turnLetter();
        service.turnLetter();
        return service;
    }

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
    public boolean turnLetter(String user) {
        boolean letterTurned = service.turnLetter();
        if (letterTurned)
            submitChatLine(user + " turned a letter");
        else
            submitChatLine("No more letters left! Game Over!");
        return letterTurned;
    }

    @Override
    public synchronized String submitWord(String user, String words) {
        String[] items = words.split(" ");
        boolean success = false;
        for (String word : items) {
            WordStatus result = service.addWord(user, word);
            String message = user + " submitted " + word;
            if (result.equals(WordStatus.OK)) {
                success = true;
                message += " - ACCEPTED";
            } else
                message += " - REJECTED - " + result;
            submitChatLine(message);
        }
        if (success)
            return WordStatus.OK.toString();
        else
            return null;
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

    @Override
    public synchronized void restart(String user) {
        submitChatLine(user + " requested a restart");
        service = createService();
    }

}