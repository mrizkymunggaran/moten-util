package moten.david.util.appengine;

/**
 * Exception to indicate that a lock could not be obtained.
 * 
 * @author dave
 * 
 */
public class CouldNotObtainLockException extends RuntimeException {

    /**
     * Constructor.
     * 
     * @param string
     */
    public CouldNotObtainLockException(String string) {
        super(string);
    }

    private static final long serialVersionUID = 1224990309334958758L;

}
