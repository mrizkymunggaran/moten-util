package moten.david.squabble;

public class DictionaryAlwaysValid implements Dictionary {

    @Override
    public boolean isValid(String word) {
        return true;
    }

}
