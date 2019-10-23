/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import java.net.URI;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormItemEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.TimeParser;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Goto;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Strategy of the FIA to execute a <code>&lt;goto&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Goto
 *
 * @author Dirk Schnelle-Walka
 */
final class GotoStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(GotoStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Goto.ATTRIBUTE_EXPR);
        EVAL_ATTRIBUTES.add(Goto.ATTRIBUTE_EXPRITEM);
    }

    /** The target of the goto. */
    private String next;

    /** The target item of the goto. */
    private String nextItem;

    /**
     * Constructs a new object.
     */
    GotoStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes(final DataModel model) throws ErrorEvent {
        next = (String) getAndCheckAttributeWithAlternativeExpr(model,
                Goto.ATTRIBUTE_NEXT, Goto.ATTRIBUTE_EXPR);
        nextItem = (String) getAndCheckAttributeWithAlternativeExpr(model,
                Goto.ATTRIBUTE_NEXTITEM, Goto.ATTRIBUTE_EXPRITEM);
        if (next == null && nextItem == null) {
            throw new BadFetchError(
                    "goto: Exactly one of 'next', 'expr', 'nextitem' or"
                            + " 'expritem' must be specified!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        if (nextItem != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("going to item '" + nextItem + "'...");
            }
            throw new GotoNextFormItemEvent(nextItem);
        }

        if (next.startsWith("#")) {
            final String nextForm = next.substring(1);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("going to form '" + nextForm + "'...");
            }
            throw new GotoNextFormEvent(nextForm);
        } else {
            final URI uri;
            try {
                uri = new URI(next);
            } catch (java.net.URISyntaxException use) {
                throw new BadFetchError(use);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("going to uri '" + uri + "'...");
            }

            final DocumentDescriptor descriptor = new DocumentDescriptor(uri,
                    DocumentDescriptor.MIME_TYPE_XML);
            final FetchAttributes attributes = getFetchAttributes();
            descriptor.setAttributes(attributes);
            final VoiceXmlDocument document = context.loadDocument(descriptor);
            final URI resolvedUri = descriptor.getUri();
            throw new GotoNextDocumentEvent(resolvedUri, document);
        }
    }

    /**
     * Determines the fetch attributes from the current node.
     * 
     * @return fetch attributes to use.
     * @since 0.7
     */
    private FetchAttributes getFetchAttributes() {
        final FetchAttributes attributes = new FetchAttributes();
        final String fetchHint = (String) getAttribute(Goto.ATTRIBUTE_FETCHHINT);
        if (fetchHint != null) {
            attributes.setFetchHint(fetchHint);
        }
        final String fetchTimeout = (String) getAttribute(Goto.ATTRIBUTE_FETCHTIMEOUT);
        if (fetchTimeout != null) {
            final TimeParser parser = new TimeParser(fetchTimeout);
            final long seconds = parser.parse();
            attributes.setFetchTimeout(seconds);
        }
        final String maxage = (String) getAttribute(Goto.ATTRIBUTE_MAXAGE);
        if (maxage != null) {
            final TimeParser parser = new TimeParser(maxage);
            final long seconds = parser.parse();
            attributes.setMaxage(seconds);
        }
        final String maxstale = (String) getAttribute(Goto.ATTRIBUTE_MAXSTALE);
        if (maxstale != null) {
            final TimeParser parser = new TimeParser(maxstale);
            final long seconds = parser.parse();
            attributes.setMaxstale(seconds);
        }
        return attributes;
    }
}
