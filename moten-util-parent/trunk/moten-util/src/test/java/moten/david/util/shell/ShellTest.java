package moten.david.util.shell;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

public class ShellTest {
    @Test
    public void test1() {
        if (Shell.isWindows())
            return;

        Shell shell = new Shell();
        ExecutorService service = Executors.newSingleThreadExecutor();
        Assert.assertEquals(0, shell.launch(service, ".", "ls"));
    }

    @Test
    public void test2() {
        if (Shell.isWindows())
            return;

        Shell shell = new Shell();
        Assert.assertEquals(0, shell.launch(".", "ls"));
    }

    @Test
    public void test3() {
        if (Shell.isWindows())
            return;

        Shell shell = new Shell();
        LineListenerRecorder recorder = new LineListenerRecorder();
        Assert.assertEquals(0, shell.launch(".", "ls", recorder));
        System.out.println(recorder);
        Assert.assertTrue(recorder.toString().contains("pom.xml"));
    }
}
