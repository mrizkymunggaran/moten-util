package moten.david.squabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.common.collect.ImmutableListMultimap;

public class Service {

    private static final int minimumChars = 3;
    private final Engine engine;
    private Data data;

    public Service(Engine engine) {
        this.engine = engine;
        User board = new User("board", 1);
        Word a = new Word(board, "a");
        Word c = new Word(board, "c");
        Word t = new Word(board, "t");

        ImmutableListMultimap<User, Word> list = ImmutableListMultimap.of(
                board, a, board, c, board, t);
        data = new Data(list);
    }

    public void addWord(String user, String word) {
        data = engine.wordSubmitted(data, new User(user, minimumChars), word);
    }

    public Data getData() {
        return data;
    }

    public static void main(String[] args) throws IOException {
        Service service = new Service(new Engine(new DictionaryAlwaysValid()));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        System.out.println("data:" + service.getData());
        while ((line = br.readLine()) != null) {
            String[] items = line.trim().split(" ");
            if (items.length >= 2) {
                String user = items[0];
                for (int i = 1; i < items.length; i++) {
                    service.addWord(user, items[i]);
                }
            }
            System.out.println("data:" + service.getData());
        }
    }

}
