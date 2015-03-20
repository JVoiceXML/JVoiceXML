/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * <p>
 * Not, that one instance of a {@link TextServer} can handle only one
 * session.
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

    /** Name of this host, maybe null to indicate the localhost. */
    private final String host;

    /** The address to use. */
    private InetAddress address;

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
    private final Object connectionLock;

    /** Lock access to wait until the server started. */
    private final Object startedLock;

    /** Registered text listeners. */
    private final Collection<TextListener> listener;

    /** <code>true</code> if a disconnect notification has been sent. */
    private boolean notifiedDisconnected;

    /** <code>true</code> if the server has been started. */
    private boolean started;

    /** <code>true</code> if the server is shutting down. */
    private boolean stopping;

    /** <code>true</code> if we received a bye from JVoiceXML. */
    private boolean receivedBye;

    /** <code>true</code> if we the client is about to close. */
    private boolean closingClient;

    /** Semaphore to notify about acknowledgments. */
    private final Object acknowledgeMonitor;

    /**
     * Constructs a new object.
     * @param hostname the hostname to use, usually this is the localhost
     * @param serverPort port number to use
     */
    public TextServer(final String hostname, final int serverPort) {
        port = serverPort;
        host = hostname;
        setDaemon(true);
        setName("JVoiceXML text server");
        listener = new java.util.ArrayList<TextListener>();

        lock = new Object();
        connectionLock = new Object();
        startedLock = new Object();
        acknowledgeMonitor = new Object();
    }

    /**
     * Constructs a new object.
     *
     * @param serverPort
     *            port number to use.
     */
    public TextServer(final int serverPort) {
        this(null, serverPort);
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
        synchronized (startedLock) {
            started = true;
            stopping = false;
            synchronized (listener) {
                for (TextListener current : listener) {
                    current.started();
                }
            }
            startedLock.notifyAll();
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
        synchronized (connectionLock) {
            connectionLock.notifyAll();
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
     * Retrieve the address to use.
     * @return the address to use
     * @throws UnknownHostException
     *         error determining the address to use
     */
    private InetAddress getAddress() throws UnknownHostException {
        if (address == null) {
            if (host == null) {
                address = InetAddress.getLocalHost();
            } else {
                address = InetAddress.getByName(host);
            }
        }
        return address;
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
                final InetAddress addr = getAddress();
                final InetSocketAddress socketAddress =
                    new InetSocketAddress(addr, port);
                callingId = TcpUriFactory.createUri(socketAddress);
                server.bind(socketAddress);
            }
        } catch (IOException | URISyntaxException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error connecting", e);
            }
            return;
        }

        LOGGER.info("text server started at port '" + port + "'");
        fireStarted();

        try {
            while (!stopping && (server != null) && !isInterrupted()) {
                client = server.accept();
                receivedBye = false;
                closingClient = false;
                final InetSocketAddress remote =
                    (InetSocketAddress) client.getRemoteSocketAddress();
                calledId = TcpUriFactory.createUri(remote);
                final InetSocketAddress local =
                    (InetSocketAddress) client.getLocalSocketAddress();
                callingId = TcpUriFactory.createUri(local);

                LOGGER.info("connected to " + calledId);
                fireConnected(remote);

                if (readOutput()) {
                    break;
                }
            }
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error reading from the socket", e);
            }
        } catch (URISyntaxException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error creating calledid or callingid", e);
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
     * @return <code>true</code> if the server should be terminated.
     * @throws IOException
     *             Error reading.
     */
    private boolean readOutput() throws IOException {
        if (client == null) {
            throw new IOException("no client connection");
        }
        final InputStream input = client.getInputStream();
        final TextMessageReader reader = new TextMessageReader(input);

        try {
            while (isConnected() && !interrupted()) {
                final TextMessage message = reader.getNextMessage();
                if (message == null) {
                    continue;
                }
                LOGGER.info("read " + message);
                final int code = message.getCode();
                if (code == TextMessage.BYE) {
                    synchronized (lock) {
                        receivedBye = true;
                        if (client != null) {
                            acknowledge(message);
                            client.close();
                            client = null;
                        }
                    }
                    fireDisconnected();
                    return true;
                }
                if (code == TextMessage.DATA) {
                    final Object data = message.getData();
                    if (data instanceof SsmlDocument) {
                        final SsmlDocument document = (SsmlDocument) data;
                        fireOutputArrived(document);
                    }
                } else if (code == TextMessage.EXPECTING_INPUT) {
                    fireExpectingInput();
                } else if (code == TextMessage.INPUT_CLOSED) {
                    fireInputClosed();
                } else if (code == TextMessage.ACK && closingClient) {
                    synchronized (acknowledgeMonitor) {
                        acknowledgeMonitor.notifyAll();
                    }
                    return true;
                }
                if (!acknowledge(message)) {
                    LOGGER.warn("can not acknowledge due to disconnect");
                    return true;
                }
            }
        } catch (IOException e) {
            if (isStarted()) {
                throw e;
            }
        } finally {
            reader.close();
        }
        return false;
    }

    /**
     * Acknowledges a message.
     * @param message the message to acknowledge
     * @return <code>true</code> if the message could be acknowledged,
     *         <code>false</code> if connection lost
     * @throws IOException sending error
     */
    private boolean acknowledge(final TextMessage message)
            throws IOException {
        if (isConnected()) {
            final int seq = message.getSequenceNumber();
            final TextMessage ack =
                    new TextMessage(TextMessage.ACK, seq);
            send(ack);
            return true;
        }
        return false;
    }

    /**
     * Waits until the text server thread has been started and is able to
     * accept incoming connections.
     * @throws InterruptedException
     *         Error waiting
     */
    public void waitStarted() throws InterruptedException {
        synchronized (startedLock) {
            if (started) {
                return;
            }
            startedLock.wait();
        }
    }

    /**
     * Waits until a connection to JVoiceXml has been established after
     * an application has been called.
     * @throws IOException
     *         Error in connection.
     */
    public void waitConnected() throws IOException {
        // we are connected if there is an active client connection
        if (client != null) {
            return;
        }

        // otherwise wait until we get one
        synchronized (connectionLock) {
            try {
                connectionLock.wait();
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
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
        // check generally if we can send
        synchronized (lock) {
            if (out == null) {
                if (isConnected()) {
                    out = client.getOutputStream();
                } else {
                    throw new IOException("Disconnected. No stream to send " +
                            message.getData());
                }
            }
            final ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(message);
            LOGGER.info("sent " + message);
        }
    }

    /**
     * Stops this server.
     */
    public void stopServer() {
        stopping = true;
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
        closingClient = true;
        synchronized (lock) {
            if (out != null) {
                try {
                    if (!receivedBye) {
                        final TextMessage message =
                                new TextMessage(TextMessage.BYE);
                        send(message);
                        synchronized (acknowledgeMonitor) {
                            acknowledgeMonitor.wait(2000);
                        }
                    }
                    out.close();
                } catch (IOException | InterruptedException e) {
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

    /**
     * Checks if the server is connected to a client.
     * @return <code>true</code> if a client is connected
     * @since 0.7.6
     */
    public boolean isConnected() {
        return (client != null) && client.isConnected();
    }
}
