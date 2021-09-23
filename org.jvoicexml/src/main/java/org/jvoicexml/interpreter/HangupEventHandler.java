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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * Default handler for events of type {@link ConnectionDisconnectHangupEvent}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class HangupEventHandler implements EventSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(HangupEventHandler.class);

    /** The used form interpretation algorithm. */
    private final FormInterpretationAlgorithm fia;
    
    /**
     * Creates a new object.
     * @param algorithm the FIA to use
     */
    public HangupEventHandler(final FormInterpretationAlgorithm algorithm) {
        fia = algorithm;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(final JVoiceXMLEvent event) {
        LOGGER.info("received hangup event '" + event + "'");
        // TODO Do nothing for now
    }

}
