/*
 * File:    $RCSfile: TextStrategy.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.implementation.SpeakablePlainText;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.SsmlParser;
import org.jvoicexml.interpreter.SsmlParsingStrategy;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.w3c.dom.Node;

/**
 * Strategy of the FIA to execute a text node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class TextStrategy
        extends AbstractTagStrategy
        implements SsmlParsingStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextStrategy.class);

    /**
     * Creates a new object.
     */
    TextStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final String text = getOutput(node);

        if (text == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring null text node");
            }

            return;
        }

        final ImplementationPlatform implementation = context
                .getImplementationPlatform();
        final SystemOutput output = implementation.borrowSystemOutput();

        final SpeakablePlainText speakable = new SpeakablePlainText(text);
        final CallControl call = implementation.borrowCallControl();
        if (call != null) {
            try {
                call.play(output, null);
            } catch (IOException e) {
                throw new BadFetchError("error playing to calling device", e);
            }
        }

        output.queueSpeakable(speakable, false, null);
    }

    /**
     * Retrieves the TTS output of this tag.
     *
     * @param node
     *            The current child node.
     * @return Output of this tag.
     */
    private String getOutput(final VoiceXmlNode node) {
        final String text = node.getNodeValue();

        if (text == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring null text node");
            }

            return null;
        }

        final String cleaned = text.trim();
        if (cleaned.length() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring empty text node");
            }

            return null;
        }

        return cleaned;
    }

    /**
     * {@inheritDoc}
     */
    public SsmlNode cloneNode(final SsmlParser parser,
            final ScriptingEngine scripting, final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode node)
        throws SemanticError {
        final String text = getOutput(node);
        if (text != null) {
            final Node textNode = document.createTextNode(text);
            parent.appendChild(textNode);
        }
        return null;
    }
}
