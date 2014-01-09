/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.mock.event;

import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Mock of an {@link EventSubscriber}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public final class MockEventSubscriber implements EventSubscriber {
    /** The last received event. */
    private JVoiceXMLEvent event;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(final JVoiceXMLEvent value) {
        event = value;
    }

    /**
     * Retrieves the last received event.
     * @return the last received event
     */
    public JVoiceXMLEvent getEvent() {
        return event;
    }
}
