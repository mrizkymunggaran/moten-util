package moten.david.util.ete;

import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;

public class UtilTest {

    @Test
    public void test() {

        TreeSet<String> set = new TreeSet<String>();

        set.add("hello");
        set.add("there");
        set.add("what");

        Assert.assertEquals("there", set.floor("under"));

    }
}
