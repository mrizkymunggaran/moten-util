package moten.david.util.shell;

/**
 * Listens for log messages
 * 
 * @author dxm
 * 
 */
public interface LineListener {
    /**
     * A line has arrived
     * 
     * @param message
     */
    void line(String message);

    /**
     * No more lines will arrive.
     */
    void finished();
}