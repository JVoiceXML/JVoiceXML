/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Collection;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.SsmlParser;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.TimeParser;
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
 * @version $Revision$
 */
class PromptStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ValueStrategy.class);

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
    public void validateAttributes()
            throws ErrorEvent {
        final String enableBargein = 
            (String) getAttribute(Prompt.ATTRIBUTE_BARGEIN);
        bargein = Boolean.valueOf(enableBargein);
    }


    /**
     * {@inheritDoc}
     *
     * Play the prompt.
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final Object cond = getAttribute(Prompt.ATTRIBUTE_COND);
        if (Boolean.FALSE.equals(cond)) {
            LOGGER.info("cond '" + cond
                    + "' evaluates to false: skipping prompt");
            return;
        }
        final SsmlParser parser = new SsmlParser(node, context);
        final SsmlDocument document;

        try {
            document = parser.getDocument();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError("Error converting to SSML!", pce);
        }
        // Set the locale
        final Speak speak = document.getSpeak();
        final String lang = (String) getAttribute(Prompt.ATTRIBUTE_XML_LANG);
        if (lang == null) {
            final Locale locale = interpreter.getLanguage();
            speak.setXmlLang(locale);
        } else {
            speak.setXmlLang(lang);
        }
        final BargeInType bargeInType = getBargeInType();
        final SpeakableSsmlText speakable =
            new SpeakableSsmlText(document, bargein, bargeInType);
        final long timeout = getTimeout();
        speakable.setTimeout(timeout);
        if (!speakable.isSpeakableTextEmpty()) {
            final ImplementationPlatform platform =
                    context.getImplementationPlatform();
            if (!fia.isQueuingPrompts()) {
                platform.setPromptTimeout(-1);
            }
            platform.queuePrompt(speakable);
            if (!fia.isQueuingPrompts()) {
                final DocumentServer server = context.getDocumentServer();
                final Session session = context.getSession();
                final String sessionId = session.getSessionID();
                platform.renderPrompts(sessionId, server);
            }
        }
    }

    /**
     * Retrieves the barge-in type.
     * @return the barge-in type.
     * @since 0.7.1
     */
    private BargeInType getBargeInType() {
        final String bargeInType =
            (String) getAttribute(Prompt.ATTRIBUTE_BARGEINTYPE);
        if (bargeInType == null) {
            return null;
        }
        final String type = bargeInType.toUpperCase();
        return BargeInType.valueOf(type);
    }

    /**
     * Retrieves the timeout attribute.
     * @return timeout to use for this prompt.
     * @since 0.7
     */
    private long getTimeout() {
        final String timeoutAttribute =
            (String) getAttribute(Prompt.ATTRIBUTE_TIMEOUT);
        if (timeoutAttribute == null) {
            return -1;
        } else {
            final TimeParser timeParser = new TimeParser(timeoutAttribute);
            return timeParser.parse();
        }
    }
}
