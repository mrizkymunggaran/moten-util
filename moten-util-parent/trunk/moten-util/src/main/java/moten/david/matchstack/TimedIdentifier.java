package moten.david.matchstack;

/**
 * An {@link Identifier} with an associated time of relevance.
 * 
 * @author dave
 * 
 */
public interface TimedIdentifier extends Timed {
    Identifier getIdentifier();
}
