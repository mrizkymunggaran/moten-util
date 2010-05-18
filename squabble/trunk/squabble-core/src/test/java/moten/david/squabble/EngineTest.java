package moten.david.squabble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class EngineTest {

    @Test
    public void test() {
        assertEquals(list("a"), list("a"));
        assertEquals(list("hello", "there"), Engine.createWordFrom(list(
                "hello", "there"), "therheello"));
        assertEquals(list("a", "b"), Engine
                .createWordFrom(list("a", "b"), "ab"));
        assertEquals(list("a", "b"), Engine
                .createWordFrom(list("a", "b"), "ba"));
        assertEquals(null, Engine.createWordFrom(list("a", "b"), "abc"));
        assertEquals(null, Engine.createWordFrom(list("a", "b"), "a"));
        assertEquals(null, Engine.createWordFrom(list("a", "b"), "b"));
        assertEquals(null, Engine.createWordFrom(list("a", "bc"), "b"));
        assertEquals(null, Engine.createWordFrom(list("a", "bb"), "b"));
        assertEquals(Sets.newHashSet(list("l", "o", "r", "e")), Sets
                .newHashSet(Engine.createWordFrom(list("a", "b", "c", "d", "e",
                        "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "r"),
                        "lore")));
        assertNull(Engine.createWordFrom(list("ab", "ra", "ca", "da", "bra"),
                "abracadabraz"));
        Iterable<Word> result = Engine.createWordFrom(list("ab", "ra", "ca",
                "da", "bra"), "abracadabr");
        assertNull(result);

        Word word = new Word(null, "par", null);
        result = Engine.createWordFrom(ImmutableList.of(word), "par");
        assertEquals(null, result);
    }

    private Iterable<Word> list(String... values) {
        List<Word> list = new ArrayList<Word>();
        for (String value : values)
            list.add(new Word(new User("anyone", 3), value, null));
        return list;
    }
}
