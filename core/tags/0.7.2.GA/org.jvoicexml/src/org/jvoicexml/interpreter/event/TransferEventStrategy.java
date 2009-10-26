/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/AudioFileOutput.java $
 * Version: $LastChangedRevision: 699 $
 * Date:    $Date: 2008-02-20 09:32:15 +0100 (Mi, 20 Feb 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.event;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.TransferEvent;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.TransferFormItem;

/**
 * Event strategy to handle the end of a transfer.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 737 $
 * @since 0.6
 */
final class TransferEventStrategy
        extends AbstractInputItemEventStrategy<TransferFormItem>
        implements EventStrategyPrototype {
    /**
     * Constructs a new object.
     */
    TransferEventStrategy() {
    }

    /**
     * Constructs a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @param interpreter
     *        The VoiceXML interpreter.
     * @param algorithm
     *        The FIA.
     * @param formItem
     *        The current form item.
     */
    public TransferEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter interpreter,
                                    final FormInterpretationAlgorithm algorithm,
                                    final FormItem formItem) {
        super(ctx, interpreter, algorithm, formItem,
                TransferEvent.EVENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleEvent(final TransferFormItem transfer,
            final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventStrategy newInstance(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia,
            final FormItem item) {
        return new TransferEventStrategy(ctx, interpreter, fia, item);
    }

}
