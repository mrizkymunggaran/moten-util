package moten.david.util.shell;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ProcessOutputRecorder implements ProcessListener {
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private final PrintWriter writer;

    public ProcessOutputRecorder() {
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                bytes)));
    }

    @Override
    public void finished() {
        writer.close();
    }

    @Override
    public void log(String message) {
        writer.println(message);
    }

    @Override
    public String toString() {
        return bytes.toString();
    }

}