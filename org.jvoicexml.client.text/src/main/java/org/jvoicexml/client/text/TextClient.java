/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.client.text.protobuf.TextMessageOuterClass.TextMessage;
import org.jvoicexml.client.text.protobuf.TextMessageOuterClass.TextMessage.TextMessageType;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A client for the text based call manager.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public final class TextClient extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(TextClient.class);;

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

    /** The last used sequence number. */
    private int lastSequenceNumber;

    /**
     * Constructs a new object.
     * 
     * @param callmanagerPort
     *            of the text based call manager
     * @throws UnknownHostException
     *             Unable to determine the local host
     */
    public TextClient(final int callmanagerPort) throws UnknownHostException {
        this(InetAddress.getLocalHost(), callmanagerPort);
    }

    /**
     * Constructs a new object.
     * 
     * @param callmanagerAddress
     *            address of the text based call manager
     * @param callmanagerPort
     *            of the text based call manager
     * @throws UnknownHostException
     *             Unable to determine the local host
     */
    public TextClient(final String callmanagerAddress, final int callmanagerPort)
            throws UnknownHostException {
        this(InetAddress.getByName(callmanagerAddress), callmanagerPort);
    }

    /**
     * Constructs a new object.
     * 
     * @param callmanagerAddress
     *            address of the text based call manager
     * @param callmanagerPort
     *            of the text based call manager
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
     * 
     * @param textListener
     *            the listener to add.
     */
    public void addTextListener(final TextListener textListener) {
        synchronized (listener) {
            listener.add(textListener);
        }
    }

    /**
     * Calls the application with the given URI.
     * 
     * @param uri
     *            the URI of the application to call
     * @throws IOException
     *             error connecting to the call manager
     */
    public void call(final URI uri) throws IOException {
        socket = new Socket(address, port);
        out = socket.getOutputStream();
        notifiedDisconnected = false;
        start();
    }

    /**
     * Hangup.
     * 
     * @throws IOException
     *             error disconnecting
     */
    public void hangup() throws IOException {
        final TextMessage bye = TextMessage.newBuilder()
                .setType(TextMessageType.BYE)
                .setSequenceNumber(lastSequenceNumber++).build();
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
        // TODO pass an actual event
        fireDisconnected(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            try {
                final InetSocketAddress remote = (InetSocketAddress) socket
                        .getRemoteSocketAddress();
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
        while ((socket != null) && socket.isConnected() && !interrupted()) {
            try {
                final TextMessage message = TextMessage.parseDelimitedFrom(in);
                LOGGER.info("read " + message);
                final TextMessageType type = message.getType();
                if (type == TextMessageType.BYE) {
                    synchronized (lock) {
                        disconnect();
                    }
                    fireDisconnected(message);
                    return;
                }
                if (type == TextMessageType.SSML) {
                    fireOutputArrived(message);
                }
                final int seq = message.getSequenceNumber();
                final TextMessage ack = TextMessage.newBuilder()
                        .setType(TextMessageType.BYE).setSequenceNumber(seq)
                        .build();
                send(ack);
            } catch (ParserConfigurationException | SAXException e) {
                throw new IOException("unable to create an SSML documet", e);
            }
        }
    }

    /**
     * Send the given input as a recognition result to JVoiceXml.
     * 
     * @param input
     *            the input to send.
     * @throws IOException
     *             Error sending the input.
     */
    public void sendInput(final String input) throws IOException {
        final TextMessage message = TextMessage.newBuilder()
                .setType(TextMessageType.USER).setData(input)
                .setSequenceNumber(lastSequenceNumber++).build();
        send(message);
    }

    /**
     * Sends the given text message to the server.
     * 
     * @param message
     *            the message to send.
     * @throws IOException
     *             error sending the message.
     */
    private void send(final TextMessage message) throws IOException {
        synchronized (lock) {
            if (out == null) {
                throw new IOException("No stream to send " + message.getData());
            }
            message.writeDelimitedTo(out);
            LOGGER.info("sent " + message);
        }
    }

    /**
     * Notifies all registered listeners that a connection has been established.
     * 
     * @param remote
     *            the address of the server.
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
     * 
     * @param message
     *            the received message
     * @throws IOException
     *             error creating the SSML document
     * @throws SAXException
     *             error creating the SSML document
     * @throws ParserConfigurationException
     *             error creating the SSML document
     */
    private void fireOutputArrived(final TextMessage message)
            throws ParserConfigurationException, SAXException, IOException {
        final String data = message.getData();
        final StringReader reader = new StringReader(data);
        final InputSource source = new InputSource(reader);
        final SsmlDocument document = new SsmlDocument(source);
        final TextMessageEvent event = new TextMessageEvent(this, message);
        synchronized (listener) {
            for (TextListener current : listener) {
                current.outputSsml(event, document);
            }
        }
    }

    /**
     * Notifies all registered listeners that a connection has been closed.
     * 
     * @param message
     *            the received message
     * @since 0.7
     */
    private void fireDisconnected(final TextMessage message) {
        if (notifiedDisconnected) {
            return;
        }
        notifiedDisconnected = true;
        final TextMessageEvent event = new TextMessageEvent(this, message);
        synchronized (listener) {
            for (TextListener current : listener) {
                current.disconnected(event);
            }
        }
    }
}
