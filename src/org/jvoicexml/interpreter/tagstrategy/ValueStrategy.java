/*
 * File:    $RCSfile: ValueStrategy.java,v $
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

import java.util.Collection;

import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.implementation.SpeakablePlainText;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.VoiceXmlNode;
import org.mozilla.javascript.Context;

/**
 * Strategy of the FIA to execute a <code>&lt;value&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Value
 *
 * @author Torben Hardt
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 */
final class ValueStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ValueStrategy.class);

    /**
     * Constructs a new object.
     */
    ValueStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * The result of the node is passed to the TTS engine.
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final String text = getOutput(context, interpreter, fia, item, node);
        if (text == null) {
            return;
        }

        final ImplementationPlatform implementation =
                context.getImplementationPlatform();

        final SynthesizedOuput output = implementation.getSystemOutput();

        final SpeakablePlainText speakable = new SpeakablePlainText(text);

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
     *
     * @exception SemanticError
     *            Error evaluating an expression.
     */
    private String getOutput(final VoiceXmlInterpreterContext context,
                             final VoiceXmlInterpreter interpreter,
                             final FormInterpretationAlgorithm fia,
                             final FormItem item,
                             final VoiceXmlNode node)
            throws SemanticError {
        final ScriptingEngine scripting = context.getScriptingEngine();
        final Value value = (Value) node;
        final Object result = scripting.eval(value.getExpr());

        if ((result == null) || (result == Context.getUndefinedValue())) {
            LOGGER.warn("ignoring empty value result");

            return null;
        }

        final String text = result.toString();
        final String cleaned = text.trim();
        if (cleaned.length() == 0) {
            LOGGER.warn("ignoring empty value node");

            return null;
        }

        return cleaned;
    }
}
