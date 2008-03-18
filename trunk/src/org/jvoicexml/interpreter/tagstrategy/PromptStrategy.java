/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
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
 * Copyright &copy; 2005-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
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
        final Object cond = getAttribute(Prompt.ATTRIBUTE_COND);
        if (Boolean.FALSE.equals(cond)) {
            LOGGER.info("cond evaluates to false: skipping prompt");
            return;
        }
        final ImplementationPlatform platform =
                context.getImplementationPlatform();
        final SystemOutput output = platform.borrowSystemOutput();
        CallControl call = null;
        try {
            final Prompt prompt = (Prompt) node;
            final SsmlParser parser = new SsmlParser(prompt, context);
            final SsmlDocument document;

            try {
                document = parser.getDocument();
            } catch (javax.xml.parsers.ParserConfigurationException pce) {
                throw new BadFetchError("Error converting to SSML!", pce);
            }

            final SpeakableSsmlText speakable = new SpeakableSsmlText(document);
            final long timeout = prompt.getTimeoutAsMsec();
            speakable.setTimeout(timeout);
            final DocumentServer documentServer = context.getDocumentServer();

            if (!speakable.isSpeakableTextEmpty()) {
                call = platform.borrowCallControl();
                output.queueSpeakable(speakable, bargein, documentServer);
                try {
                    call.play(output, null);
                } catch (IOException e) {
                    throw new BadFetchError(
                            "error playing to calling device", e);
                }
                platform.returnCallControl(call);
                call = null;
            }
        } finally {
            if (call != null) {
                platform.returnCallControl(call);
            }
            platform.returnSystemOutput(output);
        }
    }
}
