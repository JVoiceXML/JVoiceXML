/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.plain.jvxml.SubdialogResultEvent;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.SubdialogFormItem;

/**
 * Default catch event strategy for the subdialog tag.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
final class SubdialogEventStrategy
        extends AbstractInputItemEventStrategy<SubdialogFormItem>
        implements EventStrategyPrototype {
    /**
     * Constructs a new object.
     */
    SubdialogEventStrategy() {
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
    public SubdialogEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter interpreter,
                                    final FormInterpretationAlgorithm algorithm,
                                    final FormItem formItem) {
        super(ctx, interpreter, algorithm, formItem,
                SubdialogResultEvent.EVENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleEvent(final SubdialogFormItem form,
            final JVoiceXMLEvent event) throws JVoiceXMLEvent {
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
        return new SubdialogEventStrategy(ctx, interpreter, fia, item);
    }
}
