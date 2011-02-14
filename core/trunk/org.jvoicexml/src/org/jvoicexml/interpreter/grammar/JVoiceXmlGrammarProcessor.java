/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter.grammar;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.ProcessedGrammar;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.IllegalAttributeException;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * The <code>GrammarProcessor</code> is the main entry point for
 * grammar processing.<br>
 * This class provides a lean method interface to process a grammar
 * in a VoiceXML file.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class JVoiceXmlGrammarProcessor
        implements GrammarProcessor {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlGrammarProcessor.class);

    /** grammar identifier central. */
    private GrammarIdentifierCentral identifier;

    /** The grammar transformer central. */
    private GrammarTransformerCentral transformer;

    /** The cache of already processed grammars. */
    private final GrammarCache cache;

    /** The grammar loader. */
    private final GrammarLoader loader;

    /**
     * Private constructor to prevent manual instantiation.
     */
    public JVoiceXmlGrammarProcessor() {
        identifier = new GrammarIdentifierCentral();
        transformer = new GrammarTransformerCentral();
        cache = new GrammarCache();
        loader = new GrammarLoader();
    }

    /**
     * {@inheritDoc}
     * This implementation loads the {@link GrammarIdentifier}s and
     * {@link GrammarTransformer}s from the configuration. They can also be
     * added manually by
     * {@link GrammarIdentifierCentral#addIdentifier(GrammarIdentifier)} and
     * {@link GrammarTransformerCentral#addTransformer(GrammarTransformer)}.
     * TODO Rewrite the configuration to let the centrals be configured.
     */
    public void init(final Configuration configuration)
        throws ConfigurationException {
        final Collection<GrammarIdentifier> identifiers =
            configuration.loadObjects(GrammarIdentifier.class, "jvxmlgrammar");
        for (GrammarIdentifier current : identifiers) {
            identifier.addIdentifier(current);
        }
        final Collection<GrammarTransformer> transformers =
            configuration.loadObjects(GrammarTransformer.class, "jvxmlgrammar");
        for (GrammarTransformer current : transformers) {
            transformer.addTransformer(current);
        }
    }

    /**
     * Sets the central to identify grammars.
     * @param central GrammarIdentifierCentral
     * @since 0.5
     */
    public void setGrammaridentifier(final GrammarIdentifierCentral central) {
        identifier = central;
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
     */
    public ProcessedGrammar process(
            final VoiceXmlInterpreterContext context,
            final FetchAttributes attributes,
            final Grammar grammar)
            throws NoresourceError, BadFetchError, UnsupportedFormatError,
                SemanticError {
        /*
         * check if grammar is external or not an process with
         * appropriates methods
         */
        final GrammarDocument document;
        try {
            if (grammar.isExternalGrammar()) {
                document = loader.loadExternalGrammar(context, attributes,
                        grammar);
            } else {
                document = loader.loadInternalGrammar(grammar);
            }
        } catch (IllegalAttributeException e) {
            throw new BadFetchError(e.getMessage(), e);
        }

        // Identify the grammar.
        identifyGrammar(grammar, document);

        // If the grammar is already processed, we assume that this has been
        // done using the correct transformer.
        // However, it may happen, that there are different engines with
        // different formats. This may result in an error.
        if (cache.contains(document)) {
            final ProcessedGrammar processed = cache.get(document);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("grammar already processed: "
                        + document.getDocument());
                LOGGER.debug("grammar implementation: "
                        + processed.getImplementation());
            }

            return processed;
        }

        /*
         * now, we have the content of the grammar as well as the
         * type. Now transfer this grammar into a valid grammar object
         */
        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final UserInput input;
        try {
            input = platform.getUserInput();
        } catch (ConnectionDisconnectHangupEvent e) {
            throw new NoresourceError(e.getMessage(), e);
        }

        // This happens only for grammars that are defined in the form.
        final GrammarImplementation<?> grammarImpl;
        final ModeType mode = grammar.getMode();
        grammarImpl = transformer.createGrammar(input, document, mode);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("transformed grammar " + grammarImpl);
        }
        /*
         * finally add the grammar to a scoped Map
         */
        final ProcessedGrammar processed =
            new ProcessedGrammar(document, grammarImpl);
        cache.add(processed);
        return processed;
    }

    /**
     * Identifies the given grammar.
     * @param grammar the grammar to identify
     * @param document current grammar document
     * @return identified grammar document
     * @throws UnsupportedFormatError
     *         if the grammar type is not supported.
     * @since 0.7.3
     */
    private GrammarDocument identifyGrammar(final Grammar grammar,
            final GrammarDocument document) throws UnsupportedFormatError {
        // now we need to know the actual type.
        final GrammarType actualType =
                identifier.identifyGrammar(document);
        // let's check, if the declared type is supported.
        if (actualType == null) {
            throw new UnsupportedFormatError(
                    grammar.getType() + " is not supported.");
        }

        document.setMediaType(actualType);

        /**
         * @todo check preferred and declared (from the grammar object) type.
         */

        // Yes they really match. return the external grammar.
        return document;
    }
}
