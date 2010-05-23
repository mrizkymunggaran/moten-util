package moten.david.util.words;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DictionarySowpodsTest {

    private final Dictionary dictionary = new DictionarySowpods();

    @Test
    public void testDictionaryContainsAValidWord() {
        assertTrue(dictionary.isValid("garbage"));
    }

    @Test
    public void testDictionaryIsCaseInsensitive() {
        assertTrue(dictionary.isValid("GARBAGE"));
        assertTrue(dictionary.isValid("garBage"));
    }

    @Test
    public void testDictionaryDoesNotContainBogusWord() {
        assertFalse(dictionary.isValid("ZZUZZQQQ"));
    }

}
