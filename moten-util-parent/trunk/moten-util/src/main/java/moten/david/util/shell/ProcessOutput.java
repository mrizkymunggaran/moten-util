package moten.david.util.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dxm
 * 
 */
public class ProcessOutput implements Runnable {

    private static Logger log = Logger.getLogger(ProcessOutput.class.getName());

    private final InputStream is;
    private final ProcessListener listener;
    private boolean finished = false;

    public ProcessOutput(InputStream is, ProcessListener listener) {
        this.is = is;
        this.listener = listener;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                listener.log(line);
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