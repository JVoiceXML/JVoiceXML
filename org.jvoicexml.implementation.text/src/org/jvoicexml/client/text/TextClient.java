/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/src/org/jvoicexml/client/text/TextServer.java $
 * Version: $LastChangedRevision: 2846 $
 * Date:    $Date: 2011-10-17 09:29:03 +0200 (Mo, 17 Okt 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.client.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * A client for the {@link org.jvoicexml.callmanager.text.TextCallManager}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public final class TextClient extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TextClient.class);;

    /**
     * Address of the {@link org.jvoicexml.callmanager.text.TextCallManager}.
     */
    private final InetAddress address;

    /** Port of the {@link org.jvoicexml.callmanager.text.TextCallManager}. */
    private final int port;

    /** Connection to the text based callmanager. */
    private Socket socket;

    /** The stream to send the output to. */
    private OutputStream out;

    /** Lock access to the sockets. */
    private final Object lock;

    /** Registered text listeners. */
    private final Collection<TextListener> listener;

    /** <code>true</code> if a disconnect notification has been sent. */
    private boolean notifiedDisconnected;

    /**
     * Constructs a new object.
     * @param callmanagerPort of the text based call manager
     * @throws UnknownHostException
     *         Unable to determine the local host
     */
    public TextClient(final int callmanagerPort) throws UnknownHostException {
        this(InetAddress.getLocalHost(), callmanagerPort);
    }

    /**
     * Constructs a new object.
     * @param callmanagerAddress address of the text based call manager
     * @param callmanagerPort of the text based call manager
     * @throws UnknownHostException
     *         Unable to determine the local host
     */
    public TextClient(final String callmanagerAddress,
            final int callmanagerPort) throws UnknownHostException {
        this(InetAddress.getByName(callmanagerAddress), callmanagerPort);
    }

    /**
     * Constructs a new object.
     * @param callmanagerAddress address of the text based call manager
     * @param callmanagerPort of the text based call manager
     */
    public TextClient(final InetAddress callmanagerAddress,
            final int callmanagerPort) {
        port = callmanagerPort;
        address = callmanagerAddress;
        lock = new Object();
        setDaemon(true);
        setName("JVoiceXML text client");
        listener = new java.util.ArrayList<TextListener>();
    }

    /**
     * Adds the given text listener to the list of known listeners.
     * @param textListener the listener to add.
     */
    public void addTextListener(final TextListener textListener) {
        synchronized (listener) {
            listener.add(textListener);
        }
    }

    /**
     * Calls the application with the given URI.
     * @param uri the URI of the application to call
     * @throws IOException
     *         error connecting to the call manager
     */
    public void call(final URI uri) throws IOException {
        socket = new Socket(address, port);
        out = socket.getOutputStream();
        notifiedDisconnected = false;
        start();
    }

    /**
     * Hangup.
     * @throws IOException
     *         error disconnecting
     */
    public void hangup() throws IOException {
        final TextMessage bye = new TextMessage(TextMessage.BYE);
        send(bye);
        disconnect();
    }

    /**
     * Closes the client socket.
     */
    private void disconnect() {
        synchronized (lock) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.warn("error closing the client output stream", e);
                } finally {
                    out = null;
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.warn("error closing the client", e);
                } finally {
                    socket = null;
                }
            }
        }
        interrupt();
        fireDisconnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            try {
                final InetSocketAddress remote =
                        (InetSocketAddress) socket.getRemoteSocketAddress();
                fireConnected(remote);
                final URI remoteUri = TcpUriFactory.createUri(remote);
                LOGGER.info("connected to " + remoteUri);

                while ((socket != null) && !interrupted()) {
                    readOutput();
                }
            } catch (IOException e) {
                LOGGER.error("error reading from the socket", e);
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } finally {
            disconnect();
        }
    }

    /**
     * Reads the output from the VoiceXML interpreter.
     *
     * @throws IOException
     *             Error reading.
     */
    private void readOutput() throws IOException {
        if (socket == null) {
            throw new IOException("no connection");
        }
        final InputStream in = socket.getInputStream();
        final NonBlockingObjectInputStream oin =
            new NonBlockingObjectInputStream(in);
        while ((socket != null) && socket.isConnected() && !interrupted()) {
            try {
                final TextMessage message = (TextMessage) oin.readObject();
                LOGGER.info("read " + message);
                final int code = message.getCode();
                if (code == TextMessage.BYE) {
                    synchronized (lock) {
                        disconnect();
                    }
                    fireDisconnected();
                    return;
                }
                if (code == TextMessage.DATA) {
                    final Object data = message.getData();
                    if (data instanceof SsmlDocument) {
                        final SsmlDocument document = (SsmlDocument) data;
                        fireOutputArrived(document);
                    }
                }
                final int seq = message.getSequenceNumber();
                final TextMessage ack = new TextMessage(TextMessage.ACK, seq);
                send(ack);
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to instantiate the read object");
            }
        }
    }

    /**
     * Send the given input as a recognition result to JVoiceXml.
     * @param input the input to send.
     * @throws IOException
     *         Error sending the input.
     */
    public void sendInput(final String input) throws IOException {
        final TextMessage message =
            new TextMessage(TextMessage.USER, 0, input);
        send(message);
    }

    /**
     * Sends the given text message to the server.
     * @param message the message to send.
     * @throws IOException
     *         error sending the message.
     */
    private void send(final TextMessage message)
        throws IOException {
        synchronized (lock) {
            if (out == null) {
                throw new IOException("No stream to send " + message.getData());
            }
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(message);
            final byte[] bytes = bout.toByteArray();
            out.write(bytes);
            LOGGER.info("sent " + message);
        }
    }

    /**
     * Notifies all registered listeners that a connection has been established.
     * @param remote the address of the server.
     * @since 0.7
     */
    private void fireConnected(final InetSocketAddress remote) {
        notifiedDisconnected = false;
        synchronized (listener) {
            for (TextListener current : listener) {
                current.connected(remote);
            }
        }
    }

    /**
     * Notifies all registered listeners that the given SSML document has
     * arrived.
     * @param document the received document.
     */
    private void fireOutputArrived(final SsmlDocument document) {
        synchronized (listener) {
            for (TextListener current : listener) {
                current.outputSsml(document);
            }
        }
    }

    /**
     * Notifies all registered listeners that a connection has been closed.
     * @since 0.7
     */
    private void fireDisconnected() {
        if (notifiedDisconnected) {
            return;
        }
        notifiedDisconnected = true;
        synchronized (listener) {
            for (TextListener current : listener) {
                current.disconnected();
            }
        }
    }
}
