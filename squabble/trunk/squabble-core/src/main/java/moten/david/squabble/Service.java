package moten.david.squabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import moten.david.squabble.Engine.Result;
import moten.david.squabble.Engine.WordStatus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class Service {

    private static Logger log = Logger.getLogger(Service.class.getName());

    private static final int minimumChars = 3;
    private final Engine engine;
    private Data data;
    private final User board = new User("board", 1);
    private static final boolean TURN_ON_OK_WORD = false;

    public Service(Engine engine) {
        this.engine = engine;
        ListMultimap<User, Word> map = ArrayListMultimap.create();
        data = new Data(map);
        data = engine.turnLetter(data, board);
    }

    public WordStatus addWord(String user, String word) {
        Result result = engine.wordSubmitted(data,
                new User(user, minimumChars), word);
        log.info(word + " status:" + result.getStatus());
        data = result.getData();
        if (TURN_ON_OK_WORD && result.getStatus().equals(WordStatus.OK))
            turnLetter();
        return result.getStatus();
    }

    public void turnLetter() {
        data = engine.turnLetter(data, board);
    }

    public Data getData() {
        return data;
    }

    public static void main(String[] args) throws IOException {
        Service service = new Service(new Engine(new DictionaryAlwaysValid(),
                new Letters("eng")));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        System.out.println("data:\n" + service.getData());
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0)
                service.turnLetter();
            else if ("q".equals(line)) {
                log.info("exiting");
            } else {
                String[] items = line.trim().split(" ");
                if (items.length >= 2) {
                    String user = items[0];
                    for (int i = 1; i < items.length; i++) {
                        service.addWord(user, items[i]);
                    }
                }
            }
            System.out.println("data:\n" + service.getData());
        }
    }
}
