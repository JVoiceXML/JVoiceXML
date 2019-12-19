/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.TimeParser;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Script;

/**
 * Strategy of the FIA to execute a <code>&lt;script&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Script
 *
 * @author Dirk Schnelle-Walka
 */
class ScriptStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(ScriptStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    /** The URI to retrieve an external script. */
    private URI src;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Script.ATTRIBUTE_SRCEXPR);
    }

    /**
     * Constructs a new object.
     */
    ScriptStrategy() {
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
        final String srcAttribute = (String) getAttribute(Script.ATTRIBUTE_SRC);
        final String srcExprAttribute = (String) getAttribute(Script.ATTRIBUTE_SRCEXPR);
        if ((srcAttribute != null) && (srcExprAttribute != null)) {
            throw new BadFetchError(
                    "only one of src and srcexpr may be specified");
        }
        if (srcAttribute != null) {
            try {
                src = new URI(srcAttribute);
            } catch (URISyntaxException e) {
                throw new SemanticError("'" + srcAttribute
                        + "' is no valid uri!");
            }
        }

        if (srcExprAttribute != null) {
            try {
                src = new URI(srcExprAttribute);
            } catch (URISyntaxException e) {
                throw new SemanticError("'" + srcExprAttribute
                        + "' is no valid uri!");
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Evaluate the given script.
     */
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        // This should be done in the validate, but there we do not
        // have the node to check if an inline script exists.
        final String script = node.getTextContent().trim();
        if ((script.length() == 0) && (src == null)) {
            throw new BadFetchError(
                    "Exactly one of \"src\", \"srcexpr\", or an inline script "
                            + "must be specified!");
        }

        final DataModel model = context.getDataModel();
        if (src == null) {
            processInternalScript(script, model);
        } else {
            processExternalScript(context, model);
        }
    }

    /**
     * Processes an external script.
     * 
     * @param context
     *            the current VoiceXML interpreter context.
     * @param model
     *            the employed data model
     * @throws BadFetchError
     *             Error retrieving the script.
     * @throws SemanticError
     *             Error evaluating the script.
     */
    private void processExternalScript(
            final VoiceXmlInterpreterContext context, final DataModel model)
            throws BadFetchError, SemanticError {
        final DocumentServer server = context.getDocumentServer();
        final Session session = context.getSession();
        final Application application = context.getApplication();
        final URI uri;
        if (application == null) {
            uri = src;
        } else {
            uri = application.resolve(src);
        }
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri,
                DocumentDescriptor.MIME_TYPE_TEXT_JAVASCRIPT);
        final FetchAttributes attributes = getFetchAttributes();
        descriptor.setAttributes(attributes);
        final SessionIdentifier sessionId = session.getSessionId();
        final String externalScript = (String) server.getObject(sessionId,
                descriptor);
        model.evaluateExpression(externalScript, Object.class);
    }

    /**
     * Processes an internal script.
     * 
     * @param script
     *            the script to evaluate
     * @param model
     *            the employed data model
     * @throws SemanticError
     *             Error evaluating the script.
     */
    private void processInternalScript(final String script,
            final DataModel model) throws SemanticError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("evaluating internal script: " + script);
        }
        final String cleanedScript = StringEscapeUtils.unescapeXml(script);
        model.evaluateExpression(cleanedScript, Object.class);
    }

    /**
     * Determines the fetch attributes from the current node.
     * 
     * @return fetch attributes to use.
     * @since 0.7.1
     */
    private FetchAttributes getFetchAttributes() {
        final FetchAttributes attributes = new FetchAttributes();
        final String fetchHint = (String) getAttribute(Script.ATTRIBUTE_FETCHHINT);
        if (fetchHint != null) {
            attributes.setFetchHint(fetchHint);
        }
        final String fetchTimeout = (String) getAttribute(Script.ATTRIBUTE_FETCHTIMEOUT);
        if (fetchTimeout != null) {
            final TimeParser parser = new TimeParser(fetchTimeout);
            final long seconds = parser.parse();
            attributes.setFetchTimeout(seconds);
        }
        final String maxage = (String) getAttribute(Script.ATTRIBUTE_MAXAGE);
        if (maxage != null) {
            final TimeParser parser = new TimeParser(maxage);
            final long seconds = parser.parse();
            attributes.setMaxage(seconds);
        }
        final String maxstale = (String) getAttribute(Script.ATTRIBUTE_MAXSTALE);
        if (maxstale != null) {
            final TimeParser parser = new TimeParser(maxstale);
            final long seconds = parser.parse();
            attributes.setMaxstale(seconds);
        }
        return attributes;
    }
}
