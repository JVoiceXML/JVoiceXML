/**
 * 
 */
package org.jvoicexml.implementation.mrcpv2;

import java.net.URI;

import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Grammar implementation for the Mrcpv2 implementation.
 * @author Dirk Schnel-Walka
 * @param <T> the type of the grammar
 */
public class Mrcpv2GrammarImplementation<T>
    implements GrammarImplementation<T> {
    /** The URI of the grammar. */
    private final URI uri;
    /** The type of the grammar. */
    private final GrammarType type;
    /** The mode of the grammar. */
    final ModeType mode;

    /**
     * Creates a new grammar implementation.
     * @param grammarUri the URI of the grammar
     * @param grammarType the type of the grammar
     * @param modeType the mode of the grammar
     */
    public Mrcpv2GrammarImplementation(final URI grammarUri, 
            final GrammarType grammarType, final ModeType modeType) {
        uri = grammarUri;
        type = grammarType;
        mode = modeType;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getMediaType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        return mode;
    }

    /**
     * {@inheritDoc}
     * @return {code null} as this will be done by the MRCPv2 implementation.
     */
    @Override
    public T getGrammarDocument() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<T> other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        return uri.equals(other.getURI()) && type.equals(other.getMediaType())
                && mode.equals(other.getModeType());
    }
}
