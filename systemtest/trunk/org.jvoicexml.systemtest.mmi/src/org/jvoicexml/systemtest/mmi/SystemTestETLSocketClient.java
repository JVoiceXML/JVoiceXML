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
package org.jvoicexml.systemtest.mmi;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.Mmi;

/**
 * A connected ETL socket. Since the {@link javax.xml.bind.Unmarshaller}
 * reads until it receives an <code>EOS</code>, i.e. the socket closed, this
 * is a one-shot asynchronous read from the socket.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
final class SystemTestETLSocketClient extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(SystemTestETLSocketClient.class);

    /** the connected socket. */
    private final Socket socket;

    /** The MMI event listener to notify about the received event. */
    private final MMIEventListener listener;

    /**
     * Constructs a new object.
     * @param client the connected client socket.
     * @param eventListener the listener to notify about the received event
     */
    public SystemTestETLSocketClient(final Socket client,
            final MMIEventListener eventListener) {
        socket = client;
        listener = eventListener;
        setDaemon(true);
    }
    
    @Override
    public void run() {
        try {
            final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Unmarshaller unmarshaller = ctx.createUnmarshaller();
            final InputStream in = socket.getInputStream();
            if (LOGGER.isDebugEnabled()) {
                final InetSocketAddress address =
                        (InetSocketAddress) socket.getRemoteSocketAddress();
                final URI uri = TcpUriFactory.createUri(address);
                LOGGER.debug("expecting MMI events from '" + uri + "'");
            }
            final Object o = unmarshaller.unmarshal(in);
            if (o instanceof MMIEvent) {
                final MMIEvent event = (MMIEvent) o;
                LOGGER.info("received MMI event: " + event);
                listener.receivedEvent(event);
            } else {
                LOGGER.warn("received unknown MMI object: " + o);
            }
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }
}
