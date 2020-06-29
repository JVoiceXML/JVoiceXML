/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.profile.vxml21.tagstrategy;

import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.vxml.JVoiceXmlPrompt;
import org.jvoicexml.xml.vxml.PriorityType;

/**
 * A strategy for {@code <prompt>} tags with JVoiceXML extensions.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JVoiceXmlPromptStrategy extends PromptStrategy {
    /** The priority to use. */
    private PriorityType priority;
    
    /**
     * Constructs a new object.
     */
    JVoiceXmlPromptStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes(final DataModel model) throws ErrorEvent {
        super.validateAttributes(model);
        final String promptPriority = 
                (String) getAttribute(JVoiceXmlPrompt.ATTRIBUTE_PRIORITY);
        if (promptPriority == null) {
            priority = PriorityType.APPEND;
        } else {
            priority = PriorityType.valueOf(promptPriority);
        }
    }

    /**
     * {@inheritDoc}
     * Adapts the speakable with a priority before queuing it.
     * @since 0.7.9
     */
    @Override
    protected void queueSpeakable(final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia,
            final SpeakableSsmlText speakable) throws BadFetchError,
            NoresourceError, ConnectionDisconnectHangupEvent {
        speakable.setPriority(priority);
        super.queueSpeakable(context, fia, speakable);

    }
}
