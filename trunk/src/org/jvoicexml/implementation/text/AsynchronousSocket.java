/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Queue;

import org.jvoicexml.client.text.NonBlockingObjectInputStream;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Socket to read text input from and write text output to a client.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class AsynchronousSocket extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AsynchronousSocket.class);

    /** The selector we'll be monitoring. */
    private Selector selector;

    /** The connected socket channel. */
    private SocketChannel socket;

    /** Data to send to the client. */
    private final Queue<ByteBuffer> outQueue;

    /** Input buffer. */
    private NonBlockingObjectInputStream in;

    /**
     * Constructs a new object.
     */
    public AsynchronousSocket() {
        outQueue = new java.util.LinkedList<ByteBuffer>();
    }

    /**
     * Connects to the given address.
     *
     * @param address
     *            the address to connect to.
     * @throws IOException
     *             Error connecting.
     */
    public void connect(final SocketAddress address) throws IOException {
        final SelectorProvider provider = SelectorProvider.provider();
        selector = provider.openSelector();
        socket = SocketChannel.open();
        in = new NonBlockingObjectInputStream(socket);
        socket.configureBlocking(false);
        socket.connect(address);
        socket.register(selector, SelectionKey.OP_CONNECT);
        while (!socket.isConnected()) {
            selector.select();

            final Collection<SelectionKey> keys = selector.keys();
            for (SelectionKey key : keys) {
                if (key.isConnectable()) {
                    socket.finishConnect();
                }
            }
        }

        setDaemon(true);
        start();
    }

    /**
     * Checks if the socket is connected.
     *
     * @return <code>true</code> if the socket is connected.
     */
    public boolean isConnected() {
        if (socket == null) {
            return false;
        }

        return socket.isConnected();
    }

    /**
     * Checks if the socket is open.
     *
     * @return <code>true</code> if the socket is open.
     */
    public boolean isOpen() {
        if (socket == null) {
            return false;
        }

        return socket.isOpen();
    }

    /**
     * Closes this communication.
     *
     * @throws IOException
     *             Error closing the underlying socket.
     */
    public void close() throws IOException {
        if (socket == null) {
            return;
        }

        try {
            socket.close();
            selector.close();
        } finally {
            socket = null;
            selector = null;
        }
    }

    /**
     * Write the given object to the socket.
     *
     * @param object
     *            the object to write.
     * @throws IOException
     *             Error sending the object.
     */
    public void writeObject(final Object object) throws IOException {
        if (socket == null) {
            throw new IOException("Trying to write to an unconnected socket!");
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oout = new ObjectOutputStream(out);
        oout.writeObject(object);
        final byte[] bytes = out.toByteArray();
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);

        synchronized (outQueue) {
            outQueue.offer(buffer);
        }

        socket.register(selector, SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    /**
     * Reads an object from the client.
     * @return read object.
     * @throws IOException
     *         Error reading.
     * @throws ClassNotFoundException
     *         Unable to instantiate the object.
     */
    public Object readObject() throws IOException, ClassNotFoundException {
        socket.register(selector, SelectionKey.OP_READ);

        return in.readObject();
    }

    /**
     * Streams data to the server.
     * @param key current selection key.
     * @throws IOException
     *         Error writing.
     */
    private synchronized void write(final SelectionKey key) throws IOException {
        boolean sending = true;
        while (sending) {
            ByteBuffer buffer;
            synchronized (outQueue) {
                buffer = outQueue.poll();
            }
            sending = buffer != null;
            if (sending) {
                socket.write(buffer);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            while (socket.isConnected() && !interrupted()) {
                selector.select();
                if (!selector.isOpen()) {
                    return;
                }
                final Collection<SelectionKey> keys = selector.keys();
                for (SelectionKey key : keys) {
                    if (key.isReadable()) {
                        in.read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            }
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("selector terminated", e);
            }
        } catch (ClosedSelectorException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("selector terminated", e);
            }
        } finally {
            try {
                close();
            } catch (IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error closing connection", e);
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("closed connection");
            }
        }
    }
}
