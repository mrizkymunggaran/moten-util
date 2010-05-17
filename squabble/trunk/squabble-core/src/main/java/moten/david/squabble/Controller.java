package moten.david.squabble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;

public class Controller {

    public static Iterable<Word> createWordFrom(Iterable<Word> list, String word) {
        List<Word> empty = ImmutableList.of();
        return createWordFrom(empty, list, word);
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

    private Set<Character> getSet(String word) {
        com.google.common.collect.ImmutableSet.Builder<Character> builder = ImmutableSet
                .builder();
        for (Character ch : word.toCharArray())
            builder.add(ch);
        return builder.build();
    }

    private List<Character> getList(String word) {
        com.google.common.collect.ImmutableList.Builder<Character> builder = ImmutableList
                .builder();
        for (Character ch : word.toCharArray())
            builder.add(ch);
        return builder.build();
    }

    private List<Word> getCurrentWords(Data data) {
        Builder<Word> builder = ImmutableList.builder();
        for (User user : data.getMap().keys())
            builder.addAll(data.getMap().get(user));
        return builder.build();
    }

    public Data wordSubmitted(Data data, User user, String word) {
        Iterable<Word> result = createWordFrom(getCurrentWords(data), word);
        if (result == null)
            return data;
        else {
            ImmutableListMultimap<User, Word> map = addWord(data, user, word,
                    Lists.newArrayList(result));
            return new Data(map);
        }
    }

    private ImmutableListMultimap<User, Word> addWord(Data data, User user,
            String word, List<Word> parts) {
        Word w = new Word(user, word, parts);
        com.google.common.collect.ImmutableListMultimap.Builder<User, Word> m = ImmutableListMultimap
                .builder();
        for (User u : data.getMap().keys()) {
            ArrayList<Word> list = new ArrayList<Word>(data.getMap().get(u));
            for (Word part : parts)
                if (part.getOwner().equals(u))
                    list.remove(part);
            m.putAll(u, list);
        }
        m.put(user, w);
        return m.build();
    }

    private void fireMessage(String message) {

    }
}
