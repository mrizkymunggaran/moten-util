package moten.david.util.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Take the output from an input stream and report it to a ProcessListener.
 * 
 * @author dxm
 * 
 */
public class InputStreamReporter implements Runnable {

    private static Logger log = Logger.getLogger(InputStreamReporter.class.getName());

    private final InputStream is;
    private final LineListener listener;
    private boolean finished = false;

    public InputStreamReporter(InputStream is, LineListener listener) {
        this.is = is;
        this.listener = listener;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                listener.line(line);
            }
            br.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            finished = true;
            listener.finished();
        }
    }

    public boolean isFinished() {
        return finished;
    }
}