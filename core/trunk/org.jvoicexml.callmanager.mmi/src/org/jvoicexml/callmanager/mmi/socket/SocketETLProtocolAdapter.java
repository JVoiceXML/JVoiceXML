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
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.mmi.DecoratedMMIEvent;
import org.jvoicexml.callmanager.mmi.ETLProtocolAdapter;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.Mmi;

/**
 * A protocol adapter using plain sockets.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class SocketETLProtocolAdapter implements ETLProtocolAdapter {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(SocketETLProtocolAdapter.class);

    /** Registered listeners for MMI events. */
    private final Collection<MMIEventListener> listeners;

    /** The port number to listen on. */
    private int port;

    /** The server. */
    private SocketETLServer server;

    /**
     * Constructs a new object.
     */
    public SocketETLProtocolAdapter() {
        listeners = new java.util.ArrayList<MMIEventListener>();
    }

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
    public boolean isStarted() {
        return server.isAlive();
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
    public void sendMMIEvent(final Object channel, final LifeCycleEvent event)
        throws IOException {
        Socket client = null; 
        try {
            final String target = event.getTarget();
            if (target == null) {
                LOGGER.error("unable to send MMI event '" + event
                        + "'. No target.");
                return;
            }
            final URI uri = new URI(target);
            // Adapt the source address
            final String host = uri.getHost();
            final int targetPort = uri.getPort();
            client = new Socket(host, targetPort);
            final URI serverUri = server.getUri();
            if (serverUri == null) {
                final URI clientUri = TcpUriFactory.createUri(
                        client.getInetAddress());
                event.setSource(clientUri.toString());
            } else {
                event.setSource(serverUri.toString());
            }
            LOGGER.info("sending " + event + " to '" + uri + "'");

            // Send the message
            final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Marshaller marshaller = ctx.createMarshaller();
            final OutputStream out = client.getOutputStream();
            marshaller.marshal(event, out);
        } catch (JAXBException e) {
            throw new IOException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * Notifies all registered listeners about a received MMI Event.
     * @param event the event to notify
     */
    void notifyMMIEvent(final DecoratedMMIEvent event) {
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
