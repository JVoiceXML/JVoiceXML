/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.implementation.RecognitionResult;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.AbstractFormItem;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.xml.vxml.Filled;

/**
 * Strategy to process a recognition event coming from the implementation
 * platform.
 *
 * <p>
 * Process all filled elements, if the result is accepted.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class RecognitionEventStrategy
        extends AbstractEventStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RecognitionEventStrategy.class);

    /**
     * Construct a new object.
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
    public RecognitionEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter interpreter,
                                    final FormInterpretationAlgorithm algorithm,
                                    final AbstractFormItem formItem) {
        super(ctx, interpreter, algorithm, formItem, null,
              RecognitionEvent.EVENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing recognition event" + event);
        }

        final RecognitionEvent recognitionEvent = (RecognitionEvent) event;
        final RecognitionResult result =
                recognitionEvent.getRecognitionResult();

        if (!result.isAccepted()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("result not accepted");
            }

            return;
        }

        final String utterance = result.getUtterance();
        final String markname = result.getMark();

        final FieldFormItem field = (FieldFormItem) getFormItem();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting form item variable to '" + utterance + "'");
        }

        field.setFormItemVariable("'" + utterance + "'");
        if (markname != null) {
            field.setMarkname(markname);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executing filled elements...");
        }

        final Collection<Filled> filledElements = field.getFilledElements();
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();

        for (Filled filled : filledElements) {
            fia.executeChildNodes(field, filled);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done executing filled element");
        }

        fia.setJustFilled(field);
    }
}
