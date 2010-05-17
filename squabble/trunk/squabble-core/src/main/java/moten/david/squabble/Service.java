package moten.david.squabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;

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
        List<Word> list = ImmutableList.of(a, c, t);
        Builder<User, Word> builder = ImmutableListMultimap.builder();
        builder.putAll(board, list);
        data = new Data(builder.build());
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
            if ("q".equals(line))
                break;
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
