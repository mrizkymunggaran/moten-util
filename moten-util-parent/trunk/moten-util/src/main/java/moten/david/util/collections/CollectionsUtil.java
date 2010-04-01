package moten.david.util.collections;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

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

    public static <T> List<T> toList(final Enumeration<T> enumeration) {
        List<T> list = new ArrayList<T>();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }
}
