package moten.david.util.collections;

import java.util.Enumeration;
import java.util.Iterator;

public class CollectionsUtil {

    public static <T> Enumeration<T> toEnumeration(final Iterator<T> iterator) {
        return new Enumeration<T>() {

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }

    public static <T> int count(Enumeration<T> e) {
        int i = 0;
        while (e.hasMoreElements()) {
            i++;
            e.nextElement();
        }
        return i;
    }
}
