package org.jvoicexml.implementation.grammar.transformer;

import java.net.URI;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.BinaryGrammar;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.NuanceDynagramBinaryGrammarImplementation;
import org.jvoicexml.implementation.grammar.GrammarTransformer;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * A grammar transformer for Nance compiled grammars.
 *
 * @author Shuo Yang
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class NuanceDynagramBinaryGrammarTransformer
    implements GrammarTransformer {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getSourceType() {
        return GrammarType.GSL_BINARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getTargetType() {
        return GrammarType.GSL_BINARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> transformGrammar(final UserInput input,
            final GrammarDocument grammar) throws NoresourceError,
            UnsupportedFormatError, BadFetchError {
        
        final URI uri = grammar.getURI();
        final byte[] buffer = grammar.getBuffer();
        final BinaryGrammar compiledGrammar = new BinaryGrammar(uri, buffer);
        return new NuanceDynagramBinaryGrammarImplementation(compiledGrammar);
    }
}
