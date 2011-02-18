/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.implementation.grammar;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.ImplementationGrammarProcessor;

/**
 * Basic implementation of a {@link ImplementationGrammarProcessor}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public final class JVoiceXmlImplementationGrammarProcessor
        implements ImplementationGrammarProcessor {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlImplementationGrammarProcessor.class);

    /** The grammar transformer central. */
    private GrammarTransformerCentral transformer;

    /** The cache of already processed grammars. */
    private final GrammarCache cache;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlImplementationGrammarProcessor() {
        cache = new GrammarCache();
        transformer = new GrammarTransformerCentral();
    }

    /**
     * Sets the central to transform grammars.
     * @param central GrammarTransformerCentral
     * @since 0.5
     */
    public void setGrammartransformer(final GrammarTransformerCentral central) {
        transformer = central;
    }


    /**
     * {@inheritDoc}
     * This implementation loads the
     * {@link GrammarTransformer}s from the configuration. They can also be
     * added manually by
     * {@link GrammarTransformerCentral#addTransformer(GrammarTransformer)}.
     * TODO Rewrite the configuration to let the centrals be configured.
     */
    @Override
    public void init(final Configuration configuration)
        throws ConfigurationException {
        final Collection<GrammarTransformer> transformers =
            configuration.loadObjects(GrammarTransformer.class, "jvxmlgrammar");
        for (GrammarTransformer current : transformers) {
            transformer.addTransformer(current);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> process(final UserInput input,
            final GrammarDocument grammar)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        // If the grammar is already processed, we assume that this has been
        // done using the correct transformer.
        // However, it may happen, that there are different engines with
        // different formats. This may result in an error.
        if (cache.contains(grammar)) {
            final ProcessedGrammar processed = cache.get(grammar);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("grammar already processed: "
                        + grammar.getDocument());
                LOGGER.debug("grammar implementation: "
                        + processed.getImplementation());
            }

            return processed.getImplementation();
        }

        // Transform the grammar
        final GrammarImplementation<?> grammarImpl =
            transformer.createGrammar(input, grammar);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("transformed grammar " + grammarImpl);
        }

        /*
         * finally add the grammar to a scoped Map
         */
        final ProcessedGrammar processed =
            new ProcessedGrammar(grammar, grammarImpl);
        cache.add(processed);
        return processed.getImplementation();
    }
}
