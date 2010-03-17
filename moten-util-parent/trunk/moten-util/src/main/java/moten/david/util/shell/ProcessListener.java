package moten.david.util.shell;

/**
 * Listens for log messages
 * 
 * @author dxm
 * 
 */
public interface ProcessListener {
    /**
     * A stdout/stderr line has arrived from the Process.
     * 
     * @param message
     */
    void log(String message);

    /**
     * The process has finished.
     */
    void finished();
}