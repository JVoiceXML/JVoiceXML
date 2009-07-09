/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.CallControl;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.SsmlParser;
import org.jvoicexml.interpreter.SsmlParsingStrategy;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Strategy of the FIA to execute a <code>&lt;audio&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.ssml.Audio
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class AudioTagStrategy
        extends AbstractTagStrategy
        implements SsmlParsingStrategy {
    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Audio.ATTRIBUTE_EXPR);
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
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final SsmlParser parser = new SsmlParser(node, context);
        final SsmlDocument document;

        try {
            document = parser.getDocument();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError("Error converting to SSML!", pce);
        }

        final SpeakableSsmlText speakable = new SpeakableSsmlText(document);
        final DocumentServer documentServer = context.getDocumentServer();

        if (!speakable.isSpeakableTextEmpty()) {
            final ImplementationPlatform platform =
                context.getImplementationPlatform();
            final SystemOutput output = platform.getSystemOutput();
            final CallControl call = platform.getCallControl();
            try {
                call.play(output, null);
            } catch (IOException e) {
                throw new BadFetchError(
                        "error playing to calling device", e);
            }
            output.queueSpeakable(speakable, documentServer);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SsmlNode cloneNode(final SsmlParser parser,
            final ScriptingEngine scripting, final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode node)
        throws SemanticError {
        final Audio audio = (Audio) parent.addChild(Audio.TAG_NAME);

        final Collection<String> names = node.getAttributeNames();
        for (String name : names) {
            Object value = getAttribute(name);
            if (name.equals(Audio.ATTRIBUTE_EXPR)) {
                name = Audio.ATTRIBUTE_SRC;
            }
            if (value != null) {
                if (name.equals(Audio.ATTRIBUTE_SRC)) {
                    value = parser.resolve(value.toString());
                }
                audio.setAttribute(name, value.toString());
            }
        }

        return audio;
    }
}
