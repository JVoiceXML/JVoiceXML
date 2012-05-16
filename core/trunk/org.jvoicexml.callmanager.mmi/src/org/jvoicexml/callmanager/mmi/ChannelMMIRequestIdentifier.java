/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi;

import org.jvoicexml.Session;
import org.jvoicexml.mmi.events.MMIRequestIdentifier;

/**
 * Associated a communication channel object with MMI request identifiers.
 * @author Dirk Schnelle-walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class ChannelMMIRequestIdentifier extends MMIRequestIdentifier {
    /** The associated communication channel. */
    private Object channel;

    /** An associated session. */
    private Session session;

    /**
     * Constructs a new object.
     * @param reqId the request id
     * @param ctxId the context id
     */
    public ChannelMMIRequestIdentifier(final String reqId, final String ctxId) {
        super(reqId, ctxId);
    }

    /**
     * Retrieves the channel.
     * @return the channel
     */
    public Object getChannel() {
        return channel;
    }

    /**
     * Sets the channel.
     * @param ch the channel.
     */
    public void setChannel(final Object ch) {
        channel = ch;
    }

    /**
     * Retrieves the session.
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sets the session.
     * @param sess the session.
     */
    public void setSession(final Session sess) {
        session = sess;
    }
}
