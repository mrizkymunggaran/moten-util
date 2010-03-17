package moten.david.util.shell;

import java.util.logging.Logger;

public class ProcessListenerLogging implements ProcessListener {

    private static Logger log = Logger.getLogger(ProcessListenerLogging.class
            .getName());

    @Override
    public void finished() {
        log.info("finished stream logger");
    }

    @Override
    public void log(String message) {
        log.info(message);
    }

}