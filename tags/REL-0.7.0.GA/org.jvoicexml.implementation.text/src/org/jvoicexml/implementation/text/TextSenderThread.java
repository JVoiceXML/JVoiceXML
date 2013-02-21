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

package org.jvoicexml.implementation.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakablePlainText;
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
//    private final AsynchronousSocket socket;
    private final Socket socket;

    /** Reference to the telephony device. */
    private final TextTelephony telephony;

    /** Queued messages. */
    private final BlockingQueue<TextMessage> messages;

    /** Last used sequence number. */
    private int sequenceNumber;

    /**
     * Constructs a new object.
     * @param asyncSocket the socket to read from.
     * @param textTelephony telephony device.
     */
    public TextSenderThread(final Socket asyncSocket,
            final TextTelephony textTelephony) {
        socket = asyncSocket;
        telephony = textTelephony;
        messages = new java.util.concurrent.LinkedBlockingQueue<TextMessage>();
        sequenceNumber = 0;

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
        boolean bye = false;
        while (!bye) {
            TextMessage message = null;
            try {
                message = messages.take();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("sending " + message);
                }
                if (socket.isConnected()) {
                    final int seq = message.getSequenceNumber();
                    telephony.addPendingMessage(seq, message);
                    final ObjectOutputStream out =
                        new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(message);
                    out.flush();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("... done sending output");
                    }
                } else {
                    LOGGER.warn(
                            "unable to send to client: socket disconnected");
                    bye = true;
                    telephony.fireHungup();
                }
            } catch (IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error sending text message", e);
                }
                bye = true;
                telephony.fireHungup();
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error sending text message", e);
                }
                bye = true;
                telephony.fireHungup();
            }
            if (!bye) {
                bye = (message == null)
                    || (message.getCode() == TextMessage.BYE);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text sender thread stopped");
        }
    }

    /**
     * Sends the speakable to the client.
     * @param speakable the speakable to send.
     */
    public void sendData(final SpeakableText speakable) {
        final Serializable data;
        if (speakable instanceof SpeakablePlainText) {
            data = speakable.getSpeakableText();
        } else {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            data = ssml.getDocument();
        }
        final TextMessage message =
            new TextMessage(TextMessage.DATA, ++sequenceNumber, data);
        messages.add(message);
    }

    /**
     * Sends a bye message and terminates the sender thread.
     */
    public void sendBye() {
        final TextMessage message =
            new TextMessage(TextMessage.BYE);
        messages.add(message);
    }

    /**
     * Checks if there are messages to send.
     * @return <code>true</code> if there are messages to send.
     */
    public boolean isSending() {
        return !messages.isEmpty();
    }
}
