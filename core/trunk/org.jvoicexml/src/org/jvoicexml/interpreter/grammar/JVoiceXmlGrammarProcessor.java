/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.UserInput;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.ActiveGrammarSet;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.ProcessedGrammar;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
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

    /**
     * This helper has some helper methods to perform several tasks.
     */
    private final GrammarProcessorHelper helper;

    /** grammar identifier central. */
    private GrammarIdentifierCentral identifier;

    /** The grammar transformer central. */
    private GrammarTransformerCentral transformer;

    /**
     * Private constructor to prevent manual instantiation.
     */
    public JVoiceXmlGrammarProcessor() {
        helper = new GrammarProcessorHelper();
        identifier = new GrammarIdentifierCentral();
        transformer = new GrammarTransformerCentral();
    }

    /**
     * {@inheritDoc}
     */
    public void init(final JVoiceXmlConfiguration configuration) {
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
            final Grammar grammar,
            final ActiveGrammarSet grammars)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        /*
         * check if grammar is external or not an process with
         * appropriates methods
         */
        final GrammarDocument document;
        if (helper.isExternalGrammar(grammar)) {
            document = processExternalGrammar(context, attributes, grammar);
        } else {
            document = processInternalGrammar(grammar);
        }

        // If the grammar is already processed, we assume that this has been
        // done using the correct transformer.
        // However, it may happen, that there are different engines with
        // different formats. This may result in an error.
        if (grammars.contains(document)) {
            final ProcessedGrammar processed = grammars.get(document);
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

        /*
         * finally add the grammar to a scoped Map
         */
        final ProcessedGrammar processed =
            new ProcessedGrammar(document, grammarImpl);
        grammars.add(processed);
        return processed;
    }

    /**
     * Takes the route of processing an inline grammar. In fact, it
     * just identifies the grammar and puts it into a temporary
     * container. The container used is the ExternalGrammar, that can
     * hold the grammar as a String representation as well as the
     * corresponding media type, if one is applicable.
     *
     * @param grammar
     *        Takes a VoiceXML Node and processes the contained
     *        grammar.
     *
     * @return The result of the processing. A grammar
     *         representation which can be used to transform into a
     *         RuleGrammar.
     * @throws UnsupportedFormatError
     *         If the grammar could not be identified. This means, the
     *         grammar is not valid or (even worse) not supported.
     */
    private GrammarDocument processInternalGrammar(final Grammar grammar)
            throws UnsupportedFormatError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("process internal grammar");
        }

        final String grammarBuffer = grammar.toString();
        final GrammarDocument document =
            new JVoiceXmlGrammarDocument(grammarBuffer);

        final GrammarType actualType =
            identifier.identifyGrammar(document);

        if (actualType != null) {
            document.setMediaType(actualType);
        } else {
            GrammarType type = grammar.getType();
            LOGGER.warn("Unable to identify the type of the grammar. "
                    + "Traying to continue with suggested type: " + type);
            document.setMediaType(type);
        }

        return document;

    }

    /**
     * Take the route of processing an external grammar.
     *
     * @param context
     *        The current context
     * @param attributes
     *        attributes governing the fetch.
     * @param grammar
     *        The grammar to be processed.
     *
     * @return Is just the string representation of the
     *         grammar as well as the type.
     *
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    private GrammarDocument processExternalGrammar(
            final VoiceXmlInterpreterContext context,
            final FetchAttributes attributes, final Grammar grammar)
            throws BadFetchError, UnsupportedFormatError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("process external grammar");
        }

        // First of all, we need to check, if user has provided any
        // grammar type.
        final URI src;
        try {
            src = grammar.getSrcUri();
        } catch (java.net.URISyntaxException use) {
            throw new BadFetchError(use);
        }

        final FetchAttributes adaptedAttributes =
            adaptFetchAttributes(attributes, grammar);
        final GrammarDocument document =
                context.acquireExternalGrammar(src, adaptedAttributes);
        if (document == null) {
            throw new BadFetchError("Unable to load grammar '" + src + "'!");
        }

        // now we need to know the actual type.
        final GrammarType actualType =
                identifier.identifyGrammar(document);
        // let's check, if the declared type is supported.
        if (actualType == null) {
            throw new BadFetchError(grammar.getType() + " is not supported.");
        }

        document.setMediaType(actualType);

        /**
         * @todo check preferred and declared (from the grammar object) type.
         */

        // Yes they really match. return the external grammar.
        return document;
    }

    /**
     * Extracts the fetch attributes from the grammar and overrides the
     * settings of the document default fetch attributes.
     * @param docAttributes fetch attributes for the document.
     * @param grammar the current grammar.
     * @return attributes governing the fetch.
     */
    private FetchAttributes adaptFetchAttributes(
            final FetchAttributes docAttributes, final Grammar grammar) {
        final FetchAttributes attributes;
        if (docAttributes == null) {
            attributes = new FetchAttributes();
        } else {
            attributes = new FetchAttributes(docAttributes);
        }

        final String fetchHint = grammar.getFetchhint();
        if (fetchHint != null) {
            attributes.setFetchHint(fetchHint);
        }
        final long fetchTimeout = grammar.getFetchTimeoutAsMsec();
        if (fetchTimeout > 0) {
            attributes.setFetchTimeout(fetchTimeout);
        }
        final long maxAge = grammar.getMaxageAsMsec();
        if (maxAge > 0) {
            attributes.setMaxage(maxAge);
        }
        final long maxStale = grammar.getMaxageAsMsec();
        if (maxStale > 0) {
            attributes.setMaxstale(maxStale);
        }

        return attributes;
    }
}
