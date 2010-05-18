package moten.david.squabble;

/**
 * This Dictionary allows every word as valid.
 * 
 * @author dave
 * 
 */
public class DictionaryAlwaysValid implements Dictionary {

    @Override
    public boolean isValid(String word) {
        return true;
    }

}
