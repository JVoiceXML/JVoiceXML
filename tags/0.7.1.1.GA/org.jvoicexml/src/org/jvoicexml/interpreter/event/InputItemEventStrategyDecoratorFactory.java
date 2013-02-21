/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter.event;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.Record;
import org.jvoicexml.xml.vxml.Transfer;

/**
 * Factory to create an {@link org.jvoicexml.interpreter.EventStrategy} for
 * an {@link org.jvoicexml.interpreter.InputItem}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.7
 */
final class InputItemEventStrategyDecoratorFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(InputItemEventStrategyDecoratorFactory.class);

    /** Known strategies. */
    private static final Map<String, AbstractInputItemEventStrategy<?>>
        STRATEGIES;

    static {
        STRATEGIES =
            new java.util.HashMap<String, AbstractInputItemEventStrategy<?>>();
        STRATEGIES.put(Field.TAG_NAME, new RecognitionEventStrategy());
        STRATEGIES.put(ObjectTag.TAG_NAME, new ObjectTagEventStrategy());
        STRATEGIES.put(Record.TAG_NAME, new RecordingEventStrategy());
        STRATEGIES.put(Transfer.TAG_NAME, new TransferEventStrategy());
    }

    /**
     * Retrieves the input item event strategy decorator for the given input
     * item.
     * @param context the current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter the current <code>VoiceXmlInterpreter</code>
     * @param fia the current FIA.
     * @param item the input item.
     * @return strategy to use.
     * @since 0.7
     */
    public AbstractInputItemEventStrategy<?> getDecorator(
            final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final InputItem item) {
        if (item == null) {
            LOGGER.warn("can not obtain a decorator for a null input item");
            return null;
        }
        final String tagName = item.getNodeTagName();
        final AbstractInputItemEventStrategy<?> prototype =
            STRATEGIES.get(tagName);
        if (prototype == null) {
            LOGGER.warn("no decorator for tag '" + tagName + "'");
            return null;
        }
        return prototype.newInstance(context, interpreter, fia, item);
    }
}
