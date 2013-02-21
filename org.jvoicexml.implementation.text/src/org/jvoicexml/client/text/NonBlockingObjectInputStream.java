/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;

/**
 * {@link java.io.ObjectInputStream} is not usable with Java new I/O. It
 * requires a decoupling from the original stream.
 *
 * <p>
 * This stream can be used in two ways
 * <ol>
 * <li>read from an {@link InputStream}
 * (see {@link #NonBlockingObjectInputStream(InputStream)}) and</li>
 * <li>read from a {@link ReadableByteChannel}
 * (see {@link #NonBlockingObjectInputStream(ReadableByteChannel)}).</li>
 * </ol>
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class NonBlockingObjectInputStream extends InputStream {
    /** Logger for this class. */
    /** The read buffer. */
    private final ReadBuffer buffer;

    /**
     * Constructs a new object with the given input stream as a source.
     *
     * <p>
     * An object constructed using this constructor is intended to use the
     * conventional way of reading from an i{@link InputStream}.
     * </p>
     *
     * @param input the input stream to read from.
     */
    public NonBlockingObjectInputStream(final InputStream input) {
        buffer = new ReadBuffer(input);
        final Thread thread = new Thread(buffer);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Constructs a new object. In this case the data is obtained by a
     * {@link SelectionKey}.
     *
     * <p>
     * An object constructed using this constructor is intended to use
     * non-blocking I/O. The maintainer of the {@link ReadableByteChannel}
     * must feed the read buffer via the method
     * {@link #read(SelectionKey)}.
     * </p>
     *
     * @param channel the channel to read from.
     */
    public NonBlockingObjectInputStream(final ReadableByteChannel channel) {
        buffer = new ReadBuffer(channel);
    }

    /**
     * Reads the next object from the input stream. The call blocks until
     * the object is read.
     * @return read object.
     * @throws IOException
     *         Error reading
     * @throws ClassNotFoundException
     *         Unable to instantiate the object.
     */
    public Object readObject() throws IOException, ClassNotFoundException {
        final ObjectInputStream oin = new ObjectInputStream(this);
        return oin.readObject();
    }

    /**
     * Writes a piece of data that has been retrieved from a non blocking
     * IO.
     * @param key selection key for reading.
     * @exception IOException
     *            Error reading.
     */
    public void read(final SelectionKey key) throws IOException {
        buffer.read(key);
    }

    /**
     * A buffer to store the read data.
     *
     * @author Dirk Schnelle
     * @version $Revision$
     * @since 0.6
     *
     * <p>
     * Copyright &copy; 2007 JVoiceXML group - <a
     * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
     * </a>
     * </p>
     */
    private final class ReadBuffer extends ByteArrayOutputStream
        implements Runnable {
        /** Delay before trying the next read. */
        private static final int SLEEP_DELAY = 200;

        /** Size of buffer to read from the input stream. */
        private static final int READ_BUFFER_SIZE = 256;

        /** The input stream to read from. */
        private final InputStream in;

        /** The channel to read from. */
        private final ReadableByteChannel channel;

        /** Position in the buffer. */
        private int pos = 0;

        /** Lock to delay the read thread if there is no input available. */
        private final Object readLock;

        /** Caught exception from the read thread. */
        private IOException error;

        /**
         * Constructs a new object.
         * @param input the input stream to read from.
         */
        public ReadBuffer(final InputStream input) {
            in = input;
            channel = null;
            readLock = new Object();
        }

        /**
         * Constructs a new object.
         * @param readChannel the input channel to read from.
         */
        public ReadBuffer(final ReadableByteChannel readChannel) {
            in = null;
            channel = readChannel;
            readLock = new Object();
        }

        /**
         * Writes a piece of data that has been retrieved from a non blocking
         * IO.
         * @param key selection key for reading.
         * @throws IOException
         *         Error reading from the channel.
         */
        public void read(final SelectionKey key) throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
            int num = channel.read(byteBuffer);
            byte[] bytes = byteBuffer.array();

            synchronized (buf) {
                write(bytes, 0, num);
            }

            synchronized (readLock) {
                readLock.notifyAll();
            }
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            boolean connected = true;
            while (connected) {
                final byte[] bytes = new byte[READ_BUFFER_SIZE];
                try {
                    final int num = in.read(bytes);
                    if (num > 0) {
                        synchronized (buf) {
                            write(bytes, 0, num);
                        }
                    } else {
                        try {
                            Thread.sleep(SLEEP_DELAY);
                        } catch (InterruptedException e) {
                            error = new IOException(e.getMessage(), e);
                            return;
                        }
                    }
                    synchronized (readLock) {
                        readLock.notifyAll();
                    }
                } catch (IOException e) {
                    connected = false;
                    error = e;
                } finally {
                    synchronized (readLock) {
                        readLock.notifyAll();
                    }
                }
            }
        }

        /**
         * Returns an estimate of the number of bytes that can be read
         * (or skipped over) from this input stream without blocking by the
         * next invocation of a method for this input stream. The next
         * invocation might be the same thread or another thread. A single
         * read or skip of this many bytes will not block, but may read or skip
         * fewer bytes.
         * @return an estimate of the number of bytes that can be read
         * (or skipped over) from this input stream without blocking or 0 when
         * it reaches the end of the input stream.
         */
        public int available() {
            synchronized (buf) {
                return size() - pos;
            }
        }

        /**
         * Reads the next byte of data from the input stream. The value byte is
         * returned as an int in the range 0 to 255. If no byte is available
         * because the end of the stream has been reached, the value
         * <code>-1</code> is returned. This method blocks until input data is
         * available, the end of the stream is detected, or an exception is
         * thrown.
         * @return the next byte of data, or <code>-1</code> if the end of the
         *      stream is reached.
         * @throws IOException
         *         Error reading.
         */
        public int read() throws IOException {
            // Check if there is some data available.
            synchronized (readLock) {
                final int available = available();
                if (available == 0) {
                    try {
                        readLock.wait();
                    } catch (InterruptedException e) {
                        throw new IOException(e.getMessage());
                    }
                }
            }
            // Read the data, if available.
            synchronized (buf) {
                if (error != null) {
                    throw error;
                }

                final int available = available();
                if (available == 0) {
                    return -1;
                }

                // TODO Limit the buffer size.
                return buf[pos++];
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return buffer.available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return buffer.read();
    }
}
