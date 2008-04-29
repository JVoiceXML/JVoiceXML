/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.jvoicexml.RemoteClient;
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
 * The {@link RemoteClient} object that has to be passed to the
 * {@link org.jvoicexml.JVoiceXml#createSession(RemoteClient)} method can
 * be obtained via the {@link #getRemoteClient()} method.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TextServer extends Thread {
    /** The port number to use. */
    private final int port;

    /** Server socket. */
    private ServerSocket server;

    /** Socket to JVoiceXml. */
    private Socket client;

    /** The stream to send the output to. */
    private OutputStream out;

    /** Lock access to the sockets. */
    private final Object lock;

    /** Lock access to wait for connection. */
    private final Semaphore connectionLock;

    /** Registered text listeners. */
    private final Collection<TextListener> listener;

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
            // Should not happen here.
            e.printStackTrace();
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
     * Creates a remote client that can be used when creating a call.
     *
     * @return remote client.
     * @throws UnknownHostException
     *             IP address could not be determined.
     */
    public RemoteClient getRemoteClient() throws UnknownHostException {
        return new TextRemoteClient(port);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            synchronized (lock) {
                server = new ServerSocket(port);
            }
        } catch (IOException e) {
            return;
        }

        try {
            try {
                while ((server != null) && !interrupted()) {
                    client = server.accept();
                    readOutput();
                }
            } catch (IOException ignore) {
            }
        } finally {
            closeServer();
            closeClient();
        }
    }

    /**
     * Reads the output from the VoiceXML interpreter.
     *
     * @throws IOException
     *             Error reading.
     */
    private void readOutput() throws IOException {
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
                final int code = message.getCode();
                if (code == TextMessage.BYE) {
                    client.close();
                    client = null;
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
                }
                final int seq = message.getSequenceNumber();
                final TextMessage ack = new TextMessage(TextMessage.ACK, seq);
                send(ack);
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to instantiate the read object");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Waits until a connection to JVoiceXml has been established.
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
                return;
            }
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(message);
            final byte[] bytes = bout.toByteArray();
            out.write(bytes);
        }
    }

    /**
     * Stops this server.
     */
    public void stopServer() {
        closeServer();
        closeClient();
        interrupt();
    }

    /**
     * Closes the server socket.
     */
    private void closeServer() {
        synchronized (lock) {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server = null;
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
                    e.printStackTrace();
                } finally {
                    out = null;
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    client = null;
                }
            }
        }
    }
}
