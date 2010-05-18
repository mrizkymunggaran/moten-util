package moten.david.squabble;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

public class Data {
    private final ImmutableListMultimap<User, Word> map;

    public Data(ListMultimap<User, Word> map) {
        this.map = ImmutableListMultimap.copyOf(map);
    }

    public ImmutableListMultimap<User, Word> getMap() {
        return map;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (User user : map.keySet()) {
            if (s.length() > 0)
                s.append('\n');
            s.append(user.getName());
            for (Word word : map.get(user))
                s.append(" " + word.getWord());
        }
        return s.toString();
    }
}
