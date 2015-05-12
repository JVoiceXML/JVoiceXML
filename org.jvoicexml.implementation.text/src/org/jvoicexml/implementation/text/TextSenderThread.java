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

package org.jvoicexml.implementation.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.client.text.TextMessage;

/**
 * Writes asynchronously some text input to the client.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class TextSenderThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextSenderThread.class);

    /** The socket to read from. */
    private final Socket socket;

    /** Reference to the telephony device. */
    private final TextTelephony telephony;

    /** Queued messages. */
    private final BlockingQueue<PendingMessage> messages;

    /** Last used sequence number. */
    private int sequenceNumber;

    /** Wait for termination semaphore. */
    private final Object lock;

    /** {@code true} if a bye message is acknowledged in the next send. */
    private boolean acknowledgeBye;

    /**
     * Constructs a new object.
     * @param asyncSocket the socket to read from.
     * @param textTelephony telephony device.
     */
    public TextSenderThread(final Socket asyncSocket,
            final TextTelephony textTelephony) {
        socket = asyncSocket;
        telephony = textTelephony;
        messages =
            new java.util.concurrent.LinkedBlockingQueue<PendingMessage>();
        sequenceNumber = 0;
        lock = new Object();

        setDaemon(true);
        setName("TextSenderThread");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text sender thread started");
        }
        PendingMessage pending = null;
        try {
            boolean sending = true;
            while (sending && socket.isConnected() && !interrupted()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("trying to take next message");
                }
                pending = messages.take();
                telephony.addPendingMessage(pending);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("sending " + pending);
                }
                sendMessage(pending);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("... done sending output");
                }
                final int messageCode = pending.getMessageCode();
                sending = (messageCode != TextMessage.BYE)
                            || (messageCode == TextMessage.ACK
                                && acknowledgeBye);
            }
        } catch (InterruptedException | IOException e) {
            messages.clear();
            if (pending == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error sending text message", e);
                }
            } else {
                final TextMessage message = pending.getMessage();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error sending text message: " + message, e);
                }
            }
            telephony.fireHungup();
        }
        synchronized (lock) {
            lock.notifyAll();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text sender thread stopped");
        }
    }

    /**
     * Sends a message directly to the client.
     * @param pending the message to send
     * @throws IOException stream error
     * @since 0.7.6
     */
    private void sendMessage(final PendingMessage pending)
            throws IOException {
        final OutputStream stream = socket.getOutputStream();
        final ObjectOutputStream out =  new ObjectOutputStream(stream);
        final TextMessage message = pending.getMessage();
        out.writeObject(message);
        out.flush();
    }

    /**
     * Waits until the sender has been terminated.
     * @throws InterruptedException
     *         error waiting for the termination of the thread
     * @since 0.7.6
     */
    void waitSenderTerminated() throws InterruptedException {
        if (!isAlive()) {
            return;
        }
        synchronized (lock) {
            lock.wait();
        }
    }

    /**
     * Sends the speakable to the client.
     * @param speakable the speakable to send.
     */
    public void sendData(final SpeakableText speakable) {
        final Serializable data;
        if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            data = ssml.getDocument();
        } else {
            data = speakable.getSpeakableText();
        }
        final TextMessage message =
            new TextMessage(TextMessage.DATA, ++sequenceNumber, data);
        final PendingMessage pending = new PendingMessage(message, speakable);
        messages.add(pending);
    }

    /**
     * Sends a message that JVoiceXML is ready to receive input.
     *
     * @since 0.7.6
     */
    public void sendExpectingInput() {
        final TextMessage message =
                new TextMessage(TextMessage.EXPECTING_INPUT, ++sequenceNumber);
        final PendingMessage pending = new PendingMessage(message);
        messages.add(pending);
    }

    /**
     * Sends a message that JVoiceXML is ready to receive input.
     *
     * @since 0.7.6
     */
    public void sendClosedInput() {
        final TextMessage message =
                new TextMessage(TextMessage.INPUT_CLOSED, ++sequenceNumber);
        final PendingMessage pending = new PendingMessage(message);
        messages.add(pending);
    }

    /**
     * Sends a bye message and terminates the sender thread.
     */
    public void sendBye() {
        final TextMessage message =
                new TextMessage(TextMessage.BYE, ++sequenceNumber);
        final PendingMessage pending = new PendingMessage(message, null);
        messages.add(pending);
    }

    /**
     * Acknowledges the given message.
     * @param message the message to acknowledge
     */
    public void sendAck(final TextMessage message) {
        acknowledgeBye = message.getCode() == TextMessage.BYE;
        final int num = message.getSequenceNumber();
        final TextMessage ack =
                new TextMessage(TextMessage.ACK, num);
        final PendingMessage pending = new PendingMessage(ack);
        messages.add(pending);
    }
    
    /**
     * Checks if there are messages to send.
     * @return {@code true} if there are messages to send.
     */
    public boolean isSending() {
        return !messages.isEmpty();
    }
}
