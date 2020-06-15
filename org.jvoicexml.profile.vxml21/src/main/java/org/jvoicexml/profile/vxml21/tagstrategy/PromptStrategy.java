/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParser;
import org.jvoicexml.profile.vxml21.VoiceXml21SsmlParser;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.jvoicexml.xml.vxml.Prompt;

/**
 * Strategy of the FIA to execute a <code>&lt;prompt&gt;</code> node.
 * 
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Prompt
 * 
 * @author Dirk Schnelle-Walka
 */
class PromptStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(ValueStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Prompt.ATTRIBUTE_COND);
    }

    /** Flag, if bargein should be used. */
    private boolean bargein;

    /**
     * Constructs a new object.
     */
    PromptStrategy() {
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
        final String enableBargein = (String) getAttribute(Prompt.ATTRIBUTE_BARGEIN);
        // Default to bargein as true if not specified
        if (enableBargein == null) {
            bargein = true;
        } else {
            bargein = Boolean.valueOf(enableBargein);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Play the prompt.
     */
    @Override
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final Object cond = getAttribute(Prompt.ATTRIBUTE_COND);
        if (Boolean.FALSE.equals(cond)) {
            LOGGER.info("cond '" + cond
                    + "' evaluates to false: skipping prompt");
            return;
        }

        // Create an SSML document from the prompt node
        // Make sure to cast to prompt to access the correct constructor since
        // the VoiceXmlNode constructor does not keep namespaces
        final Prompt prompt = (Prompt) node;
        final Profile profile = context.getProfile();
        final SsmlParser parser = new VoiceXml21SsmlParser(profile, prompt,
                context);
        final SsmlDocument document;
        try {
            document = parser.getDocument();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError("Error converting to SSML!", pce);
        }

        // Set the locale
        final Speak speak = document.getSpeak();
        final String lang = (String) getAttribute(Prompt.ATTRIBUTE_XML_LANG);
        if (lang != null) {
            speak.setXmlLang(lang);
        }
        final BargeInType bargeInType = getBargeInType();
        final SpeakableSsmlText speakable = new SpeakableSsmlText(document,
                bargein, bargeInType);
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
    protected void queueSpeakable(final VoiceXmlInterpreterContext context,
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
     * Retrieves the barge-in type.
     * 
     * @return the barge-in type.
     * @since 0.7.1
     */
    private BargeInType getBargeInType() {
        final String bargeInType = (String) getAttribute(
                Prompt.ATTRIBUTE_BARGEINTYPE);
        if (bargeInType == null) {
            return null;
        }
        final String type = bargeInType.toUpperCase();
        return BargeInType.valueOf(type);
    }
}
