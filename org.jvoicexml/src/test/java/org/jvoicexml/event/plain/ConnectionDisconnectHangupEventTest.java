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
package org.jvoicexml.event.plain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link ConnectionDisconnectHangupEvent}
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class ConnectionDisconnectHangupEventTest {
    private static String EVENT_TYPE = ConnectionDisconnectEvent.EVENT_TYPE 
            + "." + ConnectionDisconnectHangupEvent.DETAIL;

    /**
     * Test method for {@link org.jvoicexml.event.plain.ConnectionDisconnectEvent#getEventType()}.
     */
    @Test
    public void testGetEventType() {
        final ConnectionDisconnectHangupEvent event =
                new ConnectionDisconnectHangupEvent();
        Assert.assertEquals(EVENT_TYPE, event.getEventType());
        final String message = event.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
    }

}
