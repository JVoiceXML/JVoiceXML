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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.mmi.events.CommonAttributeAdapter;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.Mmi;

/**
 * A connected ETL socket.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
final class SocketETLClient extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(SocketETLClient.class);

    /** the connected socket. */
    private final Socket socket;

    /** The socket protocol adapter. */
    private final SocketETLProtocolAdapter adapter;

    /**
     * Constructs a new object.
     * @param protocolAdapter the protocol adapter
     * @param client the connected client socket.
     */
    public SocketETLClient(final SocketETLProtocolAdapter protocolAdapter,
            final Socket client) {
        adapter = protocolAdapter;
        socket = client;
        setDaemon(true);
    }
    
    @Override
    public void run() {
        try {
            final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Unmarshaller unmarshaller = ctx.createUnmarshaller();
            final InputStream in = socket.getInputStream();
            while(!isInterrupted()) {
                final Object o = unmarshaller.unmarshal(in);
                if (o instanceof MMIEvent) {
                    final MMIEvent event = (MMIEvent) o;
                    event.setSource(this);
                    LOGGER.info("received MMI event: " + event);
                    adapter.notifyMMIEvent(event);
                } else {
                    LOGGER.warn("received unknown MMI object: " + o);
                }
            }
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Sends the MMI event to the client connect to this socket.
     * @param event the event to send.
     */
    public void send(final MMIEvent event) throws IOException {
        try {
            // Adapt the source address
            final InetAddress address = socket.getLocalAddress();
            final URI uri = TcpUriFactory.createUri(address);
            final CommonAttributeAdapter adapter =
                    new CommonAttributeAdapter(event);
            adapter.setSource(uri.toString());
            LOGGER.info("sending " + event);

            // Send the message
            final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Marshaller marshaller = ctx.createMarshaller();
            final OutputStream out = socket.getOutputStream();
            marshaller.marshal(event, out);
        } catch (JAXBException e) {
            throw new IOException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
