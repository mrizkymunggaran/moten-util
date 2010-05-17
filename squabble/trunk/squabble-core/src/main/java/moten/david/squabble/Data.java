package moten.david.squabble;

import com.google.common.collect.ImmutableListMultimap;

public class Data {
    private final ImmutableListMultimap<User, Word> map;

    public Data(ImmutableListMultimap<User, Word> map) {
        this.map = map;
    }

    public ImmutableListMultimap<User, Word> getMap() {
        return map;
    }
}
