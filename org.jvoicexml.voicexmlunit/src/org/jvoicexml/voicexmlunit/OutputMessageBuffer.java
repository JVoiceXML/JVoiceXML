/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeoutException;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Buffer of messages received so far from JVoiceXml. The buffer gets filled
 * by calls to the implemented {@link TextListener}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
class OutputMessageBuffer implements TextListener {
    /** The last received output message. */
    private SsmlDocument output;
    /** Synchronization. */
    private Object monitor;
    /** Caught event while waiting for an input. */
    private JVoiceXMLEvent event;

    /**
     * Constructs a new object.
     */
    public OutputMessageBuffer() {
        monitor = new Object();
    }

    /**
     * Retrieves the next message.
     * @return next message, <code>null</code> if the call was interrupted.
     * @throws InterruptedException
     *          interrupted while waiting
     * @throws JVoiceXMLEvent
     *          error waiting
     */
    public SsmlDocument nextMessage()
            throws InterruptedException, JVoiceXMLEvent {
        synchronized (monitor) {
            if (event != null) {
                throw event;
            }
            monitor.wait();
            if (event != null) {
                throw event;
            }
            try {
                return output;
            } finally {
                output = null;
                event = null;
            }
        }
    }

    /**
     * Retrieves the next message.
     * @return next message, <code>null</code> if the call was interrupted.
     * @param timeout the timeout to wait at max in msec, waits forever, if
     *          timeout is zero
     * @throws InterruptedException
     *         waiting interrupted
     * @throws TimeoutException
     *         waiting time exceeded
     */
    public SsmlDocument nextMessage(final long timeout)
            throws InterruptedException, TimeoutException {
        synchronized (monitor) {
            monitor.wait(timeout);
            if (output == null) {
                throw new TimeoutException("timeout of '" + timeout
                        + "' msec exceeded while waiting for next message");
            }
            try {
                return output;
            } finally {
                output = null;
            }
        }
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
    public void outputSsml(final SsmlDocument document) {
        synchronized (monitor) {
            output = document;
            monitor.notifyAll();
        }
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
        synchronized (monitor) {
            event = new ConnectionDisconnectHangupEvent();
            monitor.notifyAll();
        }
    }
}
