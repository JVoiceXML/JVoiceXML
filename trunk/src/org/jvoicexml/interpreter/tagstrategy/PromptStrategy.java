/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
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

package org.jvoicexml.interpreter.tagstrategy;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesizedOutput;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.implementation.SpeakableSsmlText;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.SsmlParser;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Prompt;

/**
 * Strategy of the FIA to execute a <code>&lt;prompt&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Prompt
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
class PromptStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(PromptStrategy.class);

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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes()
            throws ErrorEvent {
        String enableBargein = (String) getAttribute(Prompt.ATTRIBUTE_BARGEIN);
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
        final ImplementationPlatform implementation =
                context.getImplementationPlatform();
        final SynthesizedOutput output = implementation.getSystemOutput();

        if (output == null) {
            LOGGER.warn("no audio autput. cannot speak!");
            return;
        }

        final Prompt prompt = (Prompt) node;
        final SsmlParser parser = new SsmlParser(prompt, context);
        final SsmlDocument document;

        try {
            document = parser.getDocument();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError("Error converting to SSML!", pce);
        }

        final SpeakableText speakable = new SpeakableSsmlText(document);
        final DocumentServer documentServer = context.getDocumentServer();

        if (!speakable.isSpeakableTextEmpty()) {
            final CallControl call = implementation.getCallControl();
            if (call != null) {
                final URI uriForNextOutput =
                    output.getUriForNextSynthesisizedOutput();
                if (uriForNextOutput != null) {
                    try {
                        call.play(uriForNextOutput, null);
                    } catch (IOException e) {
                        throw new BadFetchError("error playing URI '"
                                + uriForNextOutput + "'", e);
                    }
                }
            }
            output.queueSpeakable(speakable, bargein, documentServer);
        }
    }
}
