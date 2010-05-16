package moten.david.squabble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;

public class Controller {

    private final ImmutableListMultimap<User, Word> map = ImmutableListMultimap
            .of();

    private ImmutableListMultimap<String, String> add(User user, String word) {
        throw new RuntimeException("not implemented");
    }

    private Iterable<Word> createWordFrom(List<Word> currentWords,
            final String word) {
        final Set<Character> wordSet = getSet(word);
        Iterable<Word> intersects = Iterables.filter(currentWords,
                new Predicate<Word>() {
                    @Override
                    public boolean apply(Word w) {
                        return w.getWord().length() <= word.length()
                                && Sets.intersection(getSet(w.getWord()),
                                        wordSet).size() > 0;
                    }
                });

        return intersects;
    }

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
            if (word.getHistory() != null)
                for (Word wd : word.getHistory())
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

    private List<Word> getCurrentWords() {
        Builder<Word> builder = ImmutableList.builder();
        for (User user : map.keys())
            builder.addAll(map.get(user));
        return builder.build();
    }

    public synchronized void wordSubmitted(User user, String word) {

    }

    private void fireMessage(String message) {

    }
}
