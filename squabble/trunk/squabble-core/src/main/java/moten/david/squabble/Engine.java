package moten.david.squabble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets.SetView;

public class Engine {

    private static Logger log = Logger.getLogger(Engine.class.getName());

    private final Dictionary dictionary;
    private final Letters letters;

    public Engine(Dictionary dictionary, Letters letters) {
        this.dictionary = dictionary;
        this.letters = letters;
    }

    public static Iterable<Word> createWordFrom(Iterable<Word> list, String word) {
        List<Word> empty = ImmutableList.of();
        List<Word> intersect = Lists.newArrayList();
        Set<String> allLetters = Sets.newHashSet();
        for (Word w : list) {
            if (toList(word).containsAll(toList(w.getWord()))) {
                intersect.add(w);
                allLetters.addAll(toList(w.getWord()));
            }
        }
        Set<String> wordLetters = Sets.newHashSet(toList(word));
        SetView<String> complement = Sets.difference(wordLetters, allLetters);
        if (complement.size() > 0)
            return null;
        else
            return createWordFrom(empty, intersect, word);
    }

    private static List<String> toList(String s) {
        List<String> list = Lists.newArrayList();
        for (Character ch : s.toCharArray())
            list.add(ch + "");
        return list;
    }

    private static Iterable<Word> createWordFrom(Iterable<Word> used,
            Iterable<Word> unused, final String word) {
        String usedJoined = sort(concatenate(used));
        if (usedJoined.length() > word.length())
            return null;
        else if (usedJoined.equals(sort(word))) {
            if (matchInHistory(used, word))
                return null;
            else
                return used;
        } else if (!unused.iterator().hasNext())
            return null;
        else {
            for (int i = 0; i < Iterables.size(unused); i++) {
                ArrayList<Word> a = Lists.newArrayList(used);
                ArrayList<Word> b = Lists.newArrayList(unused);
                Word part = b.get(i);
                a.add(part);
                b.remove(i);
                Iterable<Word> result = createWordFrom(a, b, word);
                if (result != null)
                    return result;
                a.remove(part);
                result = createWordFrom(a, b, word);
                if (result != null)
                    return result;
            }
            return null;
        }
    }

    private static boolean matchInHistory(Iterable<Word> words, String candidate) {
        for (Word word : words) {
            if (word.getWord().equals(candidate))
                return true;
            if (word.getMadeFrom() != null)
                for (Word wd : word.getMadeFrom())
                    if (matches(wd.toString(), candidate))
                        return true;
        }
        return false;
    }

    private static boolean matches(String w, String candidate) {
        Set<String> set = ImmutableSet.of(w, w + "r", w + "s", w + "er", w
                + "es", w + "d", w + "ed", w + "ing", "re" + w);
        return set.contains(candidate);
    }

    private static String concatenate(Iterable<Word> words) {
        StringBuffer s = new StringBuffer();
        for (Word str : words)
            s.append(str);
        return s.toString();
    }

    private static String sort(String word) {
        char[] a = word.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }

    private List<Word> getCurrentWords(Data data) {
        Builder<Word> builder = ImmutableList.builder();
        if (data.getMap().keySet() != null)
            for (User user : data.getMap().keySet())
                builder.addAll(data.getMap().get(user));
        return builder.build();
    }

    public static class Result {
        private final Data data;
        private final WordStatus status;

        public Data getData() {
            return data;
        }

        public WordStatus getStatus() {
            return status;
        }

        public Result(Data data, WordStatus status) {
            super();
            this.data = data;
            this.status = status;
        }
    }

    public static enum WordStatus {
        NOT_LONG_ENOUGH, NOT_IN_DICTIONARY, NOT_ANAGRAM, OK;
    }

    public Result wordSubmitted(Data data, User user, String word) {
        if (word.length() < user.getMinimumChars())
            return new Result(data, WordStatus.NOT_LONG_ENOUGH);
        if (!dictionary.isValid(word))
            return new Result(data, WordStatus.NOT_IN_DICTIONARY);
        Iterable<Word> result = createWordFrom(getCurrentWords(data), word);
        if (result == null)
            return new Result(data, WordStatus.NOT_ANAGRAM);
        else {
            ImmutableListMultimap<User, Word> map = addWord(data, user, word,
                    Lists.newArrayList(result));
            return new Result(new Data(map), WordStatus.OK);
        }
    }

    private ImmutableListMultimap<User, Word> addWord(Data data, User user,
            String word, List<Word> parts) {
        Word w = new Word(user, word, parts);
        ListMultimap<User, Word> map = ArrayListMultimap.create(data.getMap());
        for (Word part : parts) {
            map.remove(part.getOwner(), part);
        }
        map.put(user, w);
        return ImmutableListMultimap.copyOf(map);
    }

    public Data turnLetter(Data data, User board) {
        log.info("turning letter");
        List<String> used = getUsedLetters(data);
        List<String> available = Lists.newArrayList();
        available.addAll(letters.getLetters());
        for (String ch : used)
            available.remove(ch);
        int i = new Random().nextInt(available.size());
        String nextLetter = available.get(i);

        // add nextLetter to the board user
        ListMultimap<User, Word> map = ArrayListMultimap.create(data.getMap());
        map.put(board, new Word(board, nextLetter));
        log.info("added letter " + nextLetter + " to board");
        return new Data(map);
    }

    private List<String> getUsedLetters(Data data) {
        List<String> list = Lists.newArrayList();
        for (User user : data.getMap().keySet())
            for (Word word : data.getMap().get(user)) {
                String s = word.getWord();
                for (Character ch : s.toCharArray()) {
                    list.add(ch + "");
                }
            }
        return list;
    }
}
