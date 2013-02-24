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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.jvoicexml.client.text.TextMessage;

/**
 * Reads asynchronously some text input from the client.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class TextReceiverThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextReceiverThread.class);

    /** Maximum waiting time in msec. */
    private static final int MAX_WAIT = 300;

    /** The socket to read from. */
    private final Socket socket;

    /** Reference to the spoken input device. */
    private TextSpokenInput input;

    /** Reference to the telephony device. */
    private final TextTelephony telephony;

    /** Set to <code>true</code> if the receiver thread is started. */
    private boolean started;

    /** Wait for termination semaphore. */
    private final Object lock;
    
    /**
     * Constructs a new object.
     * @param asyncSocket the socket to read from.
     * @param textTelephony telephony device.
     */
    public TextReceiverThread(final Socket asyncSocket,
            final TextTelephony textTelephony) {
        socket = asyncSocket;
        telephony = textTelephony;
        lock = new Object();

        setDaemon(true);
        setName("TextReceiverThread");
    }

    /**
     * Sets the spoken input device.
     * @param spokenInput
     *        the spoken input device.
     */
    void setSpokenInput(final TextSpokenInput spokenInput) {
        input = spokenInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text receiver thread started");
        }
        synchronized (this) {
            started = true;
            notifyAll();
        }
        while (socket.isConnected() && !isInterrupted()) {
            try {
                final InputStream stream = socket.getInputStream();
                final ObjectInputStream in =
                    new ObjectInputStream(stream);
                final TextMessage message = (TextMessage) in.readObject();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("read: " + message);
                }
                final int code = message.getCode();
                if ((code == TextMessage.USER) && (input != null)) {
                    final String str = (String) message.getData();
                    input.notifyRecognitionResult(str);
                    input = null;
                } else {
                    final int sequenceNumber = message.getSequenceNumber();
                    telephony.removePendingMessage(sequenceNumber);
                }
                // Terminate this thread if a BYE has been received.
                if (code == TextMessage.BYE) {
                    break;
                }
            } catch (IOException | ClassNotFoundException  e) {
                if (LOGGER.isDebugEnabled()) {
                    if ((socket.isConnected() && !isInterrupted())) {
                        LOGGER.debug("error reading text message", e);
                    }
                }
                telephony.fireHungup();
                synchronized (lock) {
                    lock.notifyAll();
                }
                return;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text receiver thread stopped");
        }
        telephony.recordStopped();
        synchronized (lock) {
            lock.notifyAll();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text receiver thread stopped");
        }
    }

    /**
     * Waits until the receiver has been terminated.
     * @throws InterruptedException
     *         error waiting for the termination of the thread
     * @since 0.7.6
     */
    void waitReceiverTerminated() throws InterruptedException {
        synchronized (lock) {
            lock.wait();
        }
    }
    
    /**
     * Checks if the the receiver is in recording mode.
     * @return <code>true</code> if received user input is
     *         propagated to the user input.
     */
    boolean isRecording() {
        return input != null;
    }

    /**
     * Checks if the thread is started.
     * @return <code>true</code> if the thread is started.
     * @since 0.7
     */
    boolean isStarted() {
        return started;
    }

    /**
     * Delays until the receiver thread is started.
     * @exception InterruptedException
     *            waiting was interrupted.
     * @since 0.7
     */
    void waitStarted() throws InterruptedException {
        while (!isStarted()) {
            synchronized (this) {
                wait(MAX_WAIT);
            }
        }
    }
}
