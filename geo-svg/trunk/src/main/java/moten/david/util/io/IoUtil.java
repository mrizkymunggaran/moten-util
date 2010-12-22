package moten.david.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class IoUtil {

    private static final String EXCEPTION_MESSAGE_CAN_ONLY_BE_ITERATED_ONCE = "this iterable can only be iterated once";

    /**
     * Returns an {@link Iterable}&lt;String&gt; from an {@link InputStream}
     * that can be used in a for loop. For example
     * <p>
     * &nbsp;&nbsp; <code>for(String line: getLines(is))
     *              System.out.println(line);</code>
     * </p>
     * 
     * <p>
     * Note that the returned iterable can only be iterated through once. If
     * used a second time then a RuntimeException will be thrown.
     * </p>
     * <p>
     * The input stream is closed (is.close()) by this method once the last item
     * is used.
     * </p>
     * 
     * @param is
     *            the input stream
     * @return an Iterable of type String
     */
    public static final Iterable<String> getLines(final InputStream is) {
	return getLines(is, null);
    }

    /**
     * Returns an {@link Iterable}&lt;String&gt; from an {@link InputStream}
     * that can be used in a for loop. For example
     * <p>
     * &nbsp;&nbsp; <code>for(String line: getLines(is))
     *              System.out.println(line);</code>
     * </p>
     * <p>
     * Lines are skipped if they wholly match the {@link Pattern}.
     * </p>
     * <p>
     * Note that the returned iterable can only be iterated through once. If
     * used a second time then a RuntimeException will be thrown.
     * </p>
     * <p>
     * The input stream is closed (is.close()) by this method once the last item
     * is used.
     * </p>
     * 
     * @param is
     *            the input stream
     * @param skipPattern
     *            the regex {@link Pattern} that is used to identify lines to
     *            skip
     * @return an Iterable of type String
     */
    public static final Iterable<String> getLines(final InputStream is,
	    final Pattern skipPattern) {
	if (is == null)
	    throw new RuntimeException("input stream was null!");

	final BufferedReader br = new BufferedReader(new InputStreamReader(is));

	final String firstLine = nextLine(br, skipPattern);

	// declare an array to hold firstTime boolean value so that it is final
	// (and thus accessible in the anonymous class below) and mutable (to
	// prevent the iterable being used more than once)
	final boolean[] firstTime = new boolean[1];
	firstTime[0] = true;

	return new Iterable<String>() {

	    @Override
	    public Iterator<String> iterator() {
		if (!firstTime[0])
		    throw new NoSuchElementException(
			    EXCEPTION_MESSAGE_CAN_ONLY_BE_ITERATED_ONCE);
		firstTime[0] = false;
		return new Iterator<String>() {

		    private String line = firstLine;

		    @Override
		    public boolean hasNext() {
			return line != null;
		    }

		    @Override
		    public String next() {
			String result = line;
			line = nextLine(br, skipPattern);
			return result;
		    }

		    @Override
		    public void remove() {
			throw new RuntimeException("not implemented");
		    }
		};
	    }
	};

    }

    private static String nextLine(BufferedReader br, Pattern skipPattern) {
	try {
	    String line = br.readLine();
	    while (line != null && skipPattern != null
		    && skipPattern.matcher(line).matches())
		line = br.readLine();
	    if (line == null)
		br.close();
	    return line;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
}
