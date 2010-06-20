package moten.david.matchstack.types;

/**
 * An {@link Identifier} with an associated time of relevance.
 * 
 * @author dave
 * 
 */
public interface TimedIdentifier extends Timed {
    Identifier getIdentifier();
}
