/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Buffer of messages received so far from JVoiceXml. The buffer gets filled by
 * calls to the implemented {@link TextListener}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
class OutputMessageBuffer implements TextListener {
    /** Buffered documents. */
    private final BlockingQueue<BufferedSsmlDocument> documents;
    /** Caught event while waiting for an input. */
    private JVoiceXMLEvent event;
    /** Reference to the text server. */
    private final TextServer server;

    /**
     * Constructs a new object.
     * 
     * @param textServer
     *            the text server
     * @throws InterruptedException
     *             error initializing the semaphores
     */
    public OutputMessageBuffer(final TextServer textServer)
            throws InterruptedException {
        server = textServer;
        documents =
           new java.util.concurrent.LinkedBlockingQueue<BufferedSsmlDocument>();
    }

    /**
     * Retrieves the next message.
     * 
     * @return next message, <code>null</code> if the call was interrupted.
     * @throws InterruptedException
     *             interrupted while waiting
     * @throws JVoiceXMLEvent
     *             error waiting
     * @throws IOException
     *             error acknowledging the message
     */
    public SsmlDocument nextMessage() throws InterruptedException,
            JVoiceXMLEvent, IOException {
        BufferedSsmlDocument buffer = documents.take();
        if (event != null) {
            throw event;
        }
        final int num = buffer.getSequenceNumber();
        server.acknowledge(num);
        return buffer.getDocument();
    }

    /**
     * Retrieves the next message.
     * 
     * @return next message, <code>null</code> if the call was interrupted.
     * @param timeout
     *            the timeout to wait at max in msec, waits forever, if timeout
     *            is zero
     * @throws InterruptedException
     *             waiting interrupted
     * @throws TimeoutException
     *             waiting time exceeded
     * @throws JVoiceXMLEvent
     *             error waiting
     * @throws IOException
     *             error acknowledging the message
     */
    public SsmlDocument nextMessage(final long timeout)
            throws InterruptedException, TimeoutException, JVoiceXMLEvent,
            IOException {
        BufferedSsmlDocument buffer = documents.poll(timeout,
                TimeUnit.MILLISECONDS);
        if (event != null) {
            throw event;
        }
        if (buffer == null) {
            throw new TimeoutException("timeout of '" + timeout
                    + "' msec exceeded while waiting for next message");
        }
        final int num = buffer.getSequenceNumber();
        server.acknowledge(num);
        return buffer.getDocument();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void started() {
    }

    @Override
    public void connected(final InetSocketAddress remote) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputSsml(final int messageNumber,
            final SsmlDocument document) {
        BufferedSsmlDocument buffer = new BufferedSsmlDocument(document,
                messageNumber);
        documents.offer(buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expectingInput() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputClosed() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected() {
        event = new ConnectionDisconnectHangupEvent();
        BufferedSsmlDocument document = new BufferedSsmlDocument();
        documents.offer(document);
    }
}
