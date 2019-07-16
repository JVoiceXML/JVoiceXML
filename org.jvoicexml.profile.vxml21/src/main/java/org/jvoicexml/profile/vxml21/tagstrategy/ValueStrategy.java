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

import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParser;
import org.jvoicexml.profile.SsmlParsingStrategy;
import org.jvoicexml.profile.vxml21.VoiceXml21SsmlParser;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.TextContainer;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Value;

/**
 * Strategy of the FIA to execute a <code>&lt;value&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Value
 *
 * @author Torben Hardt
 * @author Dirk Schnelle-Walka
 */
public final class ValueStrategy extends AbstractTagStrategy
        implements SsmlParsingStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(ValueStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Value.ATTRIBUTE_EXPR);
    }

    /**
     * Constructs a new object.
     */
    public ValueStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     *
     * The result of the node is passed to the TTS engine.
     */
    @Override
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final String text = getOutput();
        if (text == null) {
            return;
        }

        final Profile profile = context.getProfile();
        final VoiceXml21SsmlParser parser = new VoiceXml21SsmlParser(profile, node, context);
        final SsmlDocument document;

        try {
            document = parser.getDocument();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError("Error converting to SSML!", pce);
        }
        final SpeakableSsmlText speakable = new SpeakableSsmlText(document,
                false, null);
        if (!speakable.isSpeakableTextEmpty()) {
            queueSpeakable(context, fia, speakable);
        }
    }

    /**
     * Queues the speakable to be played back in the {@link ImplementationPlatform}
     * @param context the current context
     * @param fia the current FIA
     * @param speakable the speakable to be queued
     * @exception BadFetchError
     *            error queuing the prompt
     * @exception NoresourceError
     *            Output device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     * @since 0.7.9
     */
    private void queueSpeakable(final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia,
            final SpeakableSsmlText speakable) throws BadFetchError,
            NoresourceError, ConnectionDisconnectHangupEvent {
        final ImplementationPlatform platform = context
                .getImplementationPlatform();
        if (!fia.isQueuingPrompts()) {
            platform.startPromptQueuing();
        }
        platform.queuePrompt(speakable);
        if (!fia.isQueuingPrompts()) {
            final Session session = context.getSession();
            final SessionIdentifier sessionId = session.getSessionId();
            try {
                final CallControlProperties callProps = context
                        .getCallControlProperties(fia);
                final DocumentServer server = context.getDocumentServer();
                platform.renderPrompts(sessionId, server, callProps);
            } catch (ConfigurationException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Retrieves the TTS output of this tag.
     *
     * @return Output of this tag, <code>null</code> if there is no text to
     *         output.
     *
     * @exception SemanticError
     *                Error evaluating an expression.
     */
    private String getOutput() throws SemanticError {
        final Object result = getAttribute(Value.ATTRIBUTE_EXPR);
        if (result == null) {
            LOGGER.warn("ignoring empty value result");

            return null;
        }

        final String text = result.toString();
        final String cleaned = text.trim();
        if (cleaned.length() == 0) {
            LOGGER.warn("ignoring empty value node");

            return null;
        }

        return StringEscapeUtils.unescapeXml(cleaned);
    }

    /**
     * {@inheritDoc}
     */
    public SsmlNode cloneNode(final SsmlParser parser,
            final DataModel model, final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode node)
            throws SemanticError {
        final String text = getOutput();
        if (parent instanceof TextContainer) {
            final TextContainer container = (TextContainer) parent;
            container.addText(text);
        } else {
            throw new SemanticError("Unable to add text '" + text + "' to "
                    + parent.getClass() + "!");
        }
        return null;
    }
}
