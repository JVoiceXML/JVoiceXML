/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/AudioTagStrategy.java $
 * Version: $LastChangedRevision: 2913 $
 * Date:    $Date: 2012-01-30 02:41:09 -0600 (lun, 30 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
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
 * @version $Revision: 2913 $
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

        if (speakable.isSpeakableTextEmpty()) {
            return;
        }
        final ImplementationPlatform platform =
                context.getImplementationPlatform();
        platform.setPromptTimeout(-1);
        platform.queuePrompt(speakable);
        final Session session = context.getSession();
        final String sessionId = session.getSessionID();
        try {
            final CallControlProperties callProps =
                    context.getCallControlProperties(fia);
            platform.renderPrompts(sessionId, documentServer, callProps);
        } catch (ConfigurationException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
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

        // Copy all attributes into the new node and replace the src
        // attribute by an evaluated expr attribute if applicable.
        // Also make a fully qualified URI of the src attribute.
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
