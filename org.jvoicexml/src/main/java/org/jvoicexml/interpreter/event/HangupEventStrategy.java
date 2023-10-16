/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.EventStrategy;

/**
 * An event handler to process hangup, i.e., 
 * {@link ConnectionDisconnectHangupEvent} events by default.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public final class HangupEventStrategy implements EventStrategy {
    /** The caught hangup envent. */
    private JVoiceXMLEvent hangupEvent;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return ConnectionDisconnectHangupEvent.EVENT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() throws SemanticError {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final JVoiceXMLEvent event) throws JVoiceXMLEvent {
        hangupEvent = event;
    }
    
    /**
     * Retrieves the caught hangup event.
     * @return the caught hangup event, {@code null} if none occurred
     */
    public JVoiceXMLEvent getEvent() {
        return hangupEvent;
    }
}
