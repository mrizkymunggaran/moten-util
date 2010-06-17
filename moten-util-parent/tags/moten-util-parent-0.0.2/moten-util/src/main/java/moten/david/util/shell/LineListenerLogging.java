package moten.david.util.shell;

import java.util.logging.Logger;

/**
 * Logs lines to java.util.Logging logger. Writes 'finished' when finished.
 * 
 * @author dxm
 * 
 */
public class LineListenerLogging implements LineListener {

    private static Logger log = Logger.getLogger(LineListenerLogging.class
            .getName());

    @Override
    public void finished() {
        log.info("finished");
    }

    @Override
    public void line(String message) {
        log.info(message);
    }

}