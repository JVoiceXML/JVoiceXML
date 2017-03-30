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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeoutException;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextMessageEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Monitor that enables delaying until JVoiceXML expects an input.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class InputMonitor implements TextListener {
    /** Synchronization. */
    private final Object monitor;
    /** Set to true, if input is expected. */
    private boolean expectingInput;
    /** Caught event while waiting for an input. */
    private JVoiceXMLEvent event;

    /**
     * Constructs a new object.
     */
    public InputMonitor() {
        monitor = new Object();
    }

    /**
     * Waits until JVoiceXml is expecting input.
     * 
     * @throws InterruptedException
     *             waiting interrupted
     * @throws TimeoutException
     *             input closed while waiting for input
     * @throws JVoiceXMLEvent
     *             error while waiting
     */
    public void waitUntilExpectingInput() throws InterruptedException,
            TimeoutException, JVoiceXMLEvent {
        synchronized (monitor) {
            try {
                if (expectingInput) {
                    return;
                }
                monitor.wait();
                if (event != null) {
                    throw event;
                }
                if (!expectingInput) {
                    throw new TimeoutException(
                          "input closed while waiting for expected input");
                }
            } finally {
                expectingInput = false;
            }
        }
    }

    /**
     * Waits until JVoiceXml is expecting input.
     * 
     * @param timeout
     *            the timeout to wait at max in msec, waits forever, if timeout
     *            is zero
     * @throws InterruptedException
     *             waiting interrupted
     * @throws TimeoutException
     *             waiting time exceeded
     * @throws JVoiceXMLEvent
     *             error while waiting
     */
    public void waitUntilExpectingInput(final long timeout)
            throws InterruptedException, TimeoutException, JVoiceXMLEvent {
        synchronized (monitor) {
            try {
                if (expectingInput) {
                    return;
                }
                monitor.wait(timeout);
                if (event != null) {
                    throw event;
                }
                if (!expectingInput) {
                    throw new TimeoutException(
                          "timeout of '" + timeout
                          + "' msec exceeded while waiting for expected input");
                }
            } finally {
                expectingInput = false;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void started() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(final InetSocketAddress remote) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputSsml(final TextMessageEvent evt,
            final SsmlDocument document) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expectingInput(final TextMessageEvent evt) {
        synchronized (monitor) {
            expectingInput = true;
            monitor.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputClosed(final TextMessageEvent evt) {
        synchronized (monitor) {
            expectingInput = false;
            monitor.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected(final TextMessageEvent evt) {
        synchronized (monitor) {
            event = new ConnectionDisconnectHangupEvent();
            monitor.notifyAll();
        }
    }
}
