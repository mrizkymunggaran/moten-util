package moten.david.util.monitoring.lookup;

/**
 * Looks up the unique value corresponding to the key.
 * 
 * @author dave
 * 
 */
public interface Lookup {
	String get(String context, String key);
}
