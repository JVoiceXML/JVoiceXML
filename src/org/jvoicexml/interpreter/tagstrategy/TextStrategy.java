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

import org.jvoicexml.CallControl;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.implementation.SpeakablePlainText;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.VoiceXmlNode;

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
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TextStrategy.class);

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
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final String text = getOutput(context, interpreter, fia, item, node);

        if (text == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring null text node");
            }

            return;
        }

        final ImplementationPlatform implementation =
                context.getImplementationPlatform();
        final SynthesizedOuput output = implementation.getSystemOutput();

        final SpeakablePlainText speakable = new SpeakablePlainText(text);
        final CallControl call = implementation.getCallControl();
        if (call != null) {
            final URI uriForNextOutput =
                output.getUriForNextSynthesisizedOutput();
            if (uriForNextOutput != null) {
                try {
                    call.play(uriForNextOutput);
                } catch (IOException e) {
                    throw new BadFetchError("error playing URI '"
                            + uriForNextOutput + "'", e);
                }
            }
        }

        output.queueSpeakable(speakable, false, null);
    }

    /**
     * Retrieves the TTS output of this tag.
     *
     * @param context
     *        The VoiceXML interpreter context.
     * @param interpreter
     *        The current VoiceXML interpreter.
     * @param fia
     *        The current form interpretation algorithm.
     * @param item
     *        The current form item.
     * @param node
     *        The current child node.
     * @return Output of this tag.
     */
    private String getOutput(final VoiceXmlInterpreterContext context,
                             final VoiceXmlInterpreter interpreter,
                             final FormInterpretationAlgorithm fia,
                             final FormItem item,
                             final VoiceXmlNode node) {
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
}
