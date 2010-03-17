package moten.david.util.shell;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Records all lines to an internal buffer in memory. Once the process has
 * finished the lines are available using the toString() method.
 * 
 * @author dxm
 * 
 */
public class LineListenerRecorder implements LineListener {
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private final PrintWriter writer;

    /**
     * No argument constructor.
     */
    public LineListenerRecorder() {
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                bytes)));
    }

    @Override
    public void finished() {
        writer.close();
    }

    @Override
    public void line(String message) {
        writer.println(message);
    }

    @Override
    public String toString() {
        return bytes.toString();
    }

}