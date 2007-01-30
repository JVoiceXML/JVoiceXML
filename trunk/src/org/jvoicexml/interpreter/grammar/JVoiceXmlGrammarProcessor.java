/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.GrammarRegistry;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.srgs.Grammar;

/**
 * The <code>GrammarProcessor</code> is the main entry point for
 * grammar processing.<br>
 * This class provides a lean methode interface to process a grammar
 * in a vxml file.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlGrammarProcessor
        implements GrammarProcessor {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JVoiceXmlGrammarProcessor.class);

    /**
     * This helper has some helper methodes to perform several tasks.
     */
    private final GrammarProcessorHelper helper;

    /** grammar identifier central. */
    private GrammarIdentifierCentral identifier;

    /** The grammar transormer central. */
    private GrammarTransformerCentral transformer;

    /**
     * Private constructor to prevent manual instanciation.
     */
    public JVoiceXmlGrammarProcessor() {
        helper = new GrammarProcessorHelper();
    }

    /**
     * Sets the central to identify grammars.
     * @param central GrammarIdentifierCentral
     * @since 0,5
     */
    public void setGrammaridentifier(final GrammarIdentifierCentral central) {
        identifier = central;
    }

    /**
     * Sets the central to transform grammars.
     * @param central GrammarTransformerCentral
     * @since 0,5
     */
    public void setGrammartransformer(final GrammarTransformerCentral central) {
        transformer = central;
    }

    /**
     * {@inheritDoc}
     */
    public void process(final VoiceXmlInterpreterContext context,
                        final Grammar grammar,
                        final GrammarRegistry grammars)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        /*
         * check if grammar is external or not an process with
         * appropriates methodes
         */
        final ExternalGrammar externalGrammar;
        if (helper.isExternalGrammar(grammar)) {
            externalGrammar = processExternalGrammar(context, grammar);
        } else {
            externalGrammar = processInternalGrammar(grammar);
        }

        /*
         * now, we have the content of the grammar as well as the
         * type. Now transfer this grammar into a Rulegrammar object
         */
        final RuleGrammar ruleGrammar =
                transformer.createGrammar(context, externalGrammar);

        /*
         * finally throw the grammar into a scoped Map
         */
        grammars.addGrammar(ruleGrammar);
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
     * @return ExternalGrammar The result of the processing. A grammar
     *         representation which can be used to transform into a
     *         RuleGrammar.
     * @throws UnsupportedFormatError
     *         If the grammar could ne be identified. This means, the
     *         grammar is not valid or (even worse) not supported.
     */
    private ExternalGrammar processInternalGrammar(final Grammar grammar)
            throws UnsupportedFormatError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("process internal grammar");
        }

        final String grammarBuffer = grammar.toString();
        final String actualType = identifier.identifyGrammar(grammarBuffer);

        return new ExternalGrammarImpl(actualType, grammarBuffer);

    }

    /**
     * Take the route of processing an external grammar.
     *
     * @param context
     *        The current VoiceXML interpreter context.
     * @param grammar
     *        The grammar to be processed.
     *
     * @return ExternalGrammar Is just the string reprentation of the
     *         grammar as wenn as the type.
     *
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    private ExternalGrammar processExternalGrammar(
            final VoiceXmlInterpreterContext context, final Grammar grammar)
            throws BadFetchError, UnsupportedFormatError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("process external grammar");
        }

        /*
         * First of all, we need to check, if user has provided any
         * grammar type
         */
        final String src = grammar.getSrc();
        final URI srcUri;
        try {
            srcUri = new URI(src);
        } catch (java.net.URISyntaxException use) {
            throw new BadFetchError(use);
        }

        final ExternalGrammar externalGrammar =
                context.acquireExternalGrammar(srcUri);
        if (externalGrammar == null) {
            throw new BadFetchError("Unable to load grammar '" + srcUri + "'!");
        }

        externalGrammar.setMediaType(grammar.getType());

        /** @todo check preferred type. */
        final String preferredType = grammar.getType();
        final String declaredType = externalGrammar.getMediaType();

        /* let's check, if the declared type is supported. */
        if (!identifier.typeSupported(declaredType)) {
            throw new BadFetchError(externalGrammar.getMediaType()
                                    + " is not supported.");
        }

        /* now we need to know the actual type */
        final String actualType =
                identifier.identifyGrammar(externalGrammar.getContents());

        /* does these two types match? */
        if (!declaredType.equals(actualType)) {
            /* no they do not match ERROR! */
            throw new BadFetchError("Declared '" + declaredType
                                    + "' and actual '" + actualType
                                    + "' grammar type do not match.");
        }

        /* yes they really match. return the external grammar */
        return externalGrammar;
    }
}
