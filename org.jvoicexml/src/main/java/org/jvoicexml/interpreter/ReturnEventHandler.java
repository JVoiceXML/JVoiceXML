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
package org.jvoicexml.interpreter;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.ReturnEvent;

/**
 * An Handler to catch ther {@code <return>} event from a subdialog.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
class ReturnEventHandler implements EventSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(ReturnEventHandler.class);

    /** The caught return event, */
    private ReturnEvent event;

    /**
     * Constructs a new object.
     */
    public ReturnEventHandler() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(final JVoiceXMLEvent e) {
        event = (ReturnEvent) e;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received return event: " + e);
        }
    }
    
    /**
     * Retrieves the caught return event.
     * @return the caught event
     */
    public ReturnEvent getReturnEvent() {
        // The VoiceXML spec leaves it open what should happen if there was no
        // return or exit and the dialog terminated because all forms were
        // processed. So we simply return TRUE in this case.
        if (event == null) {
            final Map<String, Object> parameters =
                    new java.util.HashMap<String, Object>();
            event = new ReturnEvent(parameters);
        }
        return event;
    }

}
