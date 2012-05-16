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

package org.jvoicexml.callmanager.mmi.socket;

import java.io.IOException;
import java.util.Collection;

import org.jvoicexml.callmanager.mmi.ETLProtocolAdapter;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.MMIEvent;

/**
 * A protocol adapter using plain sockets.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class SocketETLProtocolAdapter implements ETLProtocolAdapter {
    /** Registered listeners for MMI events. */
    private Collection<MMIEventListener> listeners;

    /** The port number to listen on. */
    private int port;

    /** The server. */
    private SocketETLServer server;

    /**
     * sets the port number to listen on.
     * @param portNumber the port number
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        server = new SocketETLServer(this, port);
        server.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMMIEvent(final Object channel, final MMIEvent event)
        throws IOException {
        final SocketETLClient client = (SocketETLClient) channel;
        client.send(event);
    }

    /**
     * Notifies all registered listeners about a received MMI Event.
     * @param event
     * @since 0.7.6
     */
    void notifyMMIEvent(final MMIEvent event) {
        synchronized (listeners) {
            for (MMIEventListener listener : listeners) {
                listener.receivedEvent(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (server != null) {
            server.interrupt();
            server = null;
        }
    }

}
