/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.grammar;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.Session;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.IllegalAttributeException;
import org.jvoicexml.xml.srgs.Grammar;

/**
 * Loads external and internal grammars.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
final class GrammarLoader {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(GrammarLoader.class);

    /**
     * Loads the document that is specified by the given grammar.
     * 
     * @param context
     *            The current context.
     * @param attributes
     *            attributes governing the fetch.
     * @param grammar
     *            The grammar to process
     * @return the transformed grammar
     * @exception BadFetchError
     *                If the document could not be fetched successfully.
     * @exception SemanticError
     *                if there was an error evaluating a scripting expression
     * @exception UnsupportedFormatError
     *                If an unsupported grammar has to be loaded.
     */
    public GrammarDocument loadGrammarDocument(
            final VoiceXmlInterpreterContext context,
            final FetchAttributes attributes, final Grammar grammar)
            throws UnsupportedFormatError, BadFetchError, SemanticError {
        try {
            if (grammar.isExternalGrammar()) {
                return loadExternalGrammar(context, attributes, grammar);
            } else {
                final Session session = context.getSession();
                final String sessionId = session.getSessionID();
                final DocumentServer server = context.getDocumentServer();
                return loadInternalGrammar(sessionId, server, grammar);
            }
        } catch (IllegalAttributeException | URISyntaxException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * Takes the route of processing an inline grammar. In fact, it just
     * identifies the grammar and puts it into a temporary container. The
     * container used is the ExternalGrammar, that can hold the grammar as a
     * String representation as well as the corresponding media type, if one is
     * applicable.
     *
     * @param sessionId
     *            the session id
     * @param server
     *            the document server
     * @param grammar
     *            takes a VoiceXML Node and processes the contained grammar.
     *
     * @return The result of the processing. A grammar representation which can
     *         be used to transform into a RuleGrammar.
     * @throws UnsupportedFormatError
     *             If the grammar could not be identified. This means, the
     *             grammar is not valid or (even worse) not supported.
     * @throws URISyntaxException
     *             error generating the URI for the document
     */
    private GrammarDocument loadInternalGrammar(final String sessionId,
            final DocumentServer server, final Grammar grammar)
            throws UnsupportedFormatError, URISyntaxException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading internal grammar");
        }
        final GrammarDocument document = new InternalGrammarDocument(grammar);
        server.addGrammarDocument(sessionId, document);
        return document;
    }

    /**
     * Take the route of processing an external grammar.
     *
     * @param context
     *            The current context
     * @param attributes
     *            attributes governing the fetch.
     * @param grammar
     *            The grammar to be processed.
     *
     * @return Is just the string representation of the grammar as well as the
     *         type.
     *
     * @throws UnsupportedFormatError
     *             If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *             If the document could not be fetched successfully.
     * @throws SemanticError
     *             if the srcexpr attribute could not be evaluated
     * @exception URISyntaxException
     *                if the URI of the external grammar could not be resolved
     */
    private GrammarDocument loadExternalGrammar(
            final VoiceXmlInterpreterContext context,
            final FetchAttributes attributes, final Grammar grammar)
            throws BadFetchError, UnsupportedFormatError, SemanticError,
            URISyntaxException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading external grammar");
        }

        // First of all, we need to check, if user has provided any
        // grammar type.
        URI src = getExternalUriSrc(grammar, context);
        if (src.getFragment() != null) {
            // TODO add support for URI fragments
            LOGGER.warn("URI fragments are currently not supported: "
                    + "ignoring fragment");
            src = new URI(src.getScheme(), src.getUserInfo(), src.getHost(),
                    src.getPort(), src.getPath(), src.getQuery(), null);
        }

        // Now load the grammar
        LOGGER.info("loading grammar from source: '" + src + "'");
        final FetchAttributes adaptedAttributes = adaptFetchAttributes(
                attributes, grammar);
        final GrammarDocument document = context.acquireExternalGrammar(src,
                adaptedAttributes);
        if (document == null) {
            throw new BadFetchError("Unable to load grammar '" + src + "'!");
        }
        return document;
    }

    /**
     * Retrieves the URI from the grammar node by either returning the src
     * attribute or by evaluating the srcexpr attribute.
     * 
     * @param grammar
     *            the current grammar node
     * @param context
     *            the VoiceXML interpreter context
     * @return grammar URI, <code>null</code> if there is no value defined
     * @throws URISyntaxException
     *             error creating an URI from the attribute
     * @throws SemanticError
     *             error evaluating the srcexpr attribute
     * @throws BadFetchError
     *             both, src and srcexpr were specified
     * @since 0.7.4
     */
    private URI getExternalUriSrc(final Grammar grammar,
            final VoiceXmlInterpreterContext context)
            throws URISyntaxException, SemanticError, BadFetchError {
        final URI src = grammar.getSrcUri();
        if (src != null) {
            return src;
        }
        final String srcexpr = grammar.getSrcexpr();
        if (srcexpr == null) {
            LOGGER.warn("unable to resolve the external URI: "
                    + "neither a src nor a srcexpr found");
            return null;
        }
        final String unescapedSrcexpr = StringEscapeUtils.unescapeXml(srcexpr);
        final DataModel model = context.getDataModel();
        final String value = model.evaluateExpression(unescapedSrcexpr,
                String.class);
        if ((value == null) || (value == model.getUndefinedValue())) {
            LOGGER.warn("srcexpr does not describe a valid uri");
            return null;
        }
        return new URI(value);
    }

    /**
     * Extracts the fetch attributes from the grammar and overrides the settings
     * of the document default fetch attributes.
     * 
     * @param docAttributes
     *            fetch attributes for the document.
     * @param grammar
     *            the current grammar.
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
