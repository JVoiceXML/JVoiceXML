/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Socket server to stream text from and to the JVoiceXML interpreter using a
 * text based client interface.
 *
 * <p>
 * After starting the server using {@link #start()}
 * and registering {@link TextListener}s using
 * {@link #addTextListener(TextListener)} the output of JVoiceXml can be
 * observed via the registered {@link TextListener}.
 * </p>
 *
 * <p>
 * The {@link ConnectionInformation} object that has to be passed to the
 * {@link org.jvoicexml.JVoiceXml#createSession(ConnectionInformation)} method can
 * be obtained via the {@link #getConnectionInformation()} method.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TextServer extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TextServer.class);

        /** The port number to use. */
    private final int port;

    /** Server socket. */
    private ServerSocket server;

    /** URI representation of the server's address. */
    private URI callingId;

    /** Socket to JVoiceXml. */
    private Socket client;

    /** URI representation of the client's address. */
    private URI calledId;

    /** The stream to send the output to. */
    private OutputStream out;

    /** Lock access to the sockets. */
    private final Object lock;

    /** Lock access to wait for connection. */
    private final Semaphore connectionLock;

    /** Registered text listeners. */
    private final Collection<TextListener> listener;

    /** <code>true</code> if a disconnect notification has been sent. */
    private boolean notifiedDisconnected;

    /** <code>true</code> if the server has been started. */
    private boolean started;

    /**
     * Constructs a new object.
     *
     * @param serverPort
     *            port number to use.
     */
    public TextServer(final int serverPort) {
        port = serverPort;

        setDaemon(true);
        setName("JVoiceXML text server");
        listener = new java.util.ArrayList<TextListener>();

        lock = new Object();
        connectionLock = new Semaphore(1);
        try {
            connectionLock.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("error acquiring connection lock", e);
        }
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
     * Notifies all registered listeners that the server is waiting for
     * incoming connections.
     * @since 0.7
     */
    private void fireStarted() {
        synchronized (listener) {
            for (TextListener current : listener) {
                current.started();
            }
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
     * Notifies all registered listeners that the given text has arrived.
     * @param text the received text.
     */
    private void fireOutputArrived(final String text) {
        synchronized (listener) {
            for (TextListener current : listener) {
                current.outputText(text);
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
     * Notifies all registered listeners that it is OK to send input.
     */
    private void fireExpectingInput() {
        synchronized (listener) {
            for (TextListener current : listener) {
                current.expectingInput();
            }
        }
    }

    /**
     * Notifies all registered listeners that it is no longer OK to send input.
     */
    private void fireInputClosed() {
        synchronized (listener) {
            for (TextListener current : listener) {
                current.inputClosed();
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

    /**
     * Creates a connection information container that can be used when
     * making a call.
     *
     * @return connection information
     * @throws UnknownHostException
     *             IP address could not be determined.
     */
    public ConnectionInformation getConnectionInformation()
        throws UnknownHostException {
        final TextConnectionInformation remote =
            new TextConnectionInformation(port);
        remote.setCalledDevice(calledId);
        remote.setCallingDevice(callingId);
        return remote;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            synchronized (lock) {
                server = new ServerSocket();
                server.setReuseAddress(true);
                final InetAddress localhost = InetAddress.getLocalHost();
                final InetSocketAddress address =
                    new InetSocketAddress(localhost, port);
                callingId = TcpUriFactory.createUri(address);
                server.bind(address);
            }
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error connecting", e);
            }
            return;
        } catch (URISyntaxException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error connecting", e);
            }
            return;
        }

        LOGGER.info("text server started at port '" + port + "'");
        fireStarted();
        started = true;

        try {
            try {
                while ((server != null) && !interrupted()) {
                    client = server.accept();
                    final InetSocketAddress remote =
                        (InetSocketAddress) client.getRemoteSocketAddress();
                    calledId = TcpUriFactory.createUri(remote);
                    final InetSocketAddress local =
                        (InetSocketAddress) client.getLocalSocketAddress();
                    callingId = TcpUriFactory.createUri(local);
                    LOGGER.info("connected to " + calledId);
                    fireConnected(remote);
                    readOutput();
                }
            } catch (IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error reading from the socket", e);
                }
            } catch (URISyntaxException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error creating calledid or callingid", e);
                }
            }
        } finally {
            closeServer();
            closeClient();
        }
    }

    /**
     * Checks if the server has been started.
     * @return <code>true</code> if the server has been started
     * @since 0.7.6
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Reads the output from the VoiceXML interpreter.
     *
     * @throws IOException
     *             Error reading.
     */
    private void readOutput() throws IOException {
        if (client == null) {
            throw new IOException("no client connection");
        }
        final InputStream in = client.getInputStream();
        out = client.getOutputStream();
        // We have to do the release here, since ObjectInputStream blocks
        // until the server has sent something.
        connectionLock.release();

        final NonBlockingObjectInputStream oin =
            new NonBlockingObjectInputStream(in);
        while ((client != null) && client.isConnected() && !interrupted()) {
            try {
                final TextMessage message = (TextMessage) oin.readObject();
                LOGGER.info("read " + message);
                final int code = message.getCode();
                if (code == TextMessage.BYE) {
                    synchronized (lock) {
                        if (client != null) {
                            client.close();
                            client = null;
                        }
                    }
                    fireDisconnected();
                    return;
                }
                if (code == TextMessage.DATA) {
                    final Object data = message.getData();
                    if (data instanceof String) {
                        final String text = (String) data;
                        fireOutputArrived(text);
                    } else if (data instanceof SsmlDocument) {
                        final SsmlDocument document = (SsmlDocument) data;
                        fireOutputArrived(document);
                    }
                } else if (code == TextMessage.EXPECTING_INPUT) {
                    fireExpectingInput();
                } else if (code == TextMessage.INPUT_CLOSED) {
                    fireInputClosed();
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
     * Waits until a connection to JVoiceXml has been established after
     * an application has been called.
     * @throws IOException
     *         Error in connection.
     */
    public void waitConnected() throws IOException {
        try {
            connectionLock.acquire();
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
        connectionLock.release();
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
     * Stops this server.
     */
    public void stopServer() {
        closeServer();
        closeClient();
        interrupt();
        LOGGER.info("text server stopped");
    }

    /**
     * Closes the server socket.
     */
    private void closeServer() {
        synchronized (lock) {
            if (server != null) {
                try {
                    server.close();
                    LOGGER.info("server closed");
                } catch (IOException e) {
                    LOGGER.warn("error closing the server", e);
                } finally {
                    server = null;
                    started = false;
                }
            }
        }
    }

    /**
     * Closes the client socket.
     */
    private void closeClient() {
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
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    LOGGER.warn("error closing the client", e);
                } finally {
                    client = null;
                }
            }
        }
        fireDisconnected();
    }
}
