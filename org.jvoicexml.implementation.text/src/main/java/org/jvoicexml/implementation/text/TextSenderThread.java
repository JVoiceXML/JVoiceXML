/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2018 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.client.text.protobuf.TextMessageOuterClass.TextMessage;
import org.jvoicexml.client.text.protobuf.TextMessageOuterClass.TextMessage.TextMessageType;

/**
 * Writes asynchronously some text input to the client.
 *
 * @author Dirk Schnelle-Walka
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
    
    /** {@code true} if the sender thread is running. */
    private boolean sending;

    /**
     * Constructs a new object.
     * @param asyncSocket the socket to read from.
     * @param textTelephony telephony device.
     */
    TextSenderThread(final Socket asyncSocket,
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
        sending = true;
        PendingMessage pending = null;
        try {
            while (sending && socket.isConnected() && !interrupted()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("trying to take next message");
                }
                pending = messages.take();
                final TextMessageType type = pending.getMessageCode();
                if (type != TextMessageType.ACK) {
                    telephony.addPendingMessage(pending);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("sending " + pending);
                }
                sendMessage(pending);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("... done sending output");
                }
                sending = (type != TextMessageType.BYE)
                            || (type == TextMessageType.ACK
                                && acknowledgeBye);
            }
        } catch (InterruptedException ignore) {
            messages.clear();
        } catch (IOException e) {
            final TextMessage message = pending.getMessage();
            LOGGER.warn("error sending text message: " + message, e);
            telephony.fireHungup();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text sender thread stopped");
        }
        synchronized (lock) {
            sending = false;
            lock.notifyAll();
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
        if (socket.isClosed()) {
            return;
        }
        final OutputStream out = socket.getOutputStream();
        final TextMessage message = pending.getMessage();
        message.writeDelimitedTo(out);
    }

    /**
     * Waits until the sender has been terminated.
     * @throws InterruptedException
     *         error waiting for the termination of the thread
     * @since 0.7.6
     */
    void waitSenderTerminated() throws InterruptedException {
        synchronized (lock) {
            if (!isAlive() || !sending) {
                return;
            }
            lock.wait();
        }
    }

    /**
     * Sends the speakable to the client.
     * @param speakable the speakable to send.
     */
    public void sendData(final SpeakableText speakable) {
        final String ssml = speakable.getSpeakableText();
        final TextMessage message = TextMessage.newBuilder()
                .setType(TextMessageType.SSML).setData(ssml)
                .setSequenceNumber(sequenceNumber++).build();
        final PendingMessage pending = new PendingMessage(message, speakable);
        messages.add(pending);
    }

    /**
     * Sends a message that JVoiceXML is ready to receive input.
     *
     * @since 0.7.6
     */
    public void sendExpectingInput() {
        final TextMessage message = TextMessage.newBuilder()
                .setType(TextMessageType.EXPECTING_INPUT)
                .setSequenceNumber(sequenceNumber++).build();
        final PendingMessage pending = new PendingMessage(message);
        messages.add(pending);
    }

    /**
     * Sends a message that JVoiceXML is ready to receive input.
     *
     * @since 0.7.6
     */
    public void sendClosedInput() {
        final TextMessage message = TextMessage.newBuilder()
                .setType(TextMessageType.INPUT_CLOSED)
                .setSequenceNumber(sequenceNumber++).build();
        final PendingMessage pending = new PendingMessage(message);
        messages.add(pending);
    }

    /**
     * Sends a bye message and terminates the sender thread.
     */
    public void sendBye() {
        final TextMessage message = TextMessage.newBuilder()
                .setType(TextMessageType.BYE)
                .setSequenceNumber(sequenceNumber++).build();
        final PendingMessage pending = new PendingMessage(message, null);
        messages.add(pending);
    }

    /**
     * Acknowledges the given message.
     * @param message the message to acknowledge
     */
    public void sendAck(final TextMessage message) {
        acknowledgeBye = message.getType() == TextMessageType.BYE;
        final int num = message.getSequenceNumber();
        final TextMessage ack = TextMessage.newBuilder()
                .setType(TextMessageType.ACK)
                .setSequenceNumber(num).build();
        final PendingMessage pending = new PendingMessage(ack);
        messages.add(pending);
    }
    
    /**
     * Checks if there are messages to send.
     * @return {@code true} if there are messages to send.
     */
    public boolean isSending() {
        return sending;
    }
}
