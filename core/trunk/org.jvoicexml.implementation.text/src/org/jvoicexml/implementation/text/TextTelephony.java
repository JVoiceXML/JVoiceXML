/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.text;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.callmanager.text.ConnectedTextConnectionInformation;
import org.jvoicexml.client.text.TextConnectionInformation;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;

/**
 * Text based implementation of {@link Telephony}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TextTelephony implements Telephony, ObservableTelephony {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextTelephony.class);

    /** Maximal number of milliseconds to wait for a connect. */
    private static final int MAX_TIMEOUT_CONNECT = 1000;

    /** The connection to the client. */
    private Socket socket;

    /** Receiver for messages from the client. */
    private TextReceiverThread receiver;

    /** Sender for messages to the client. */
    private TextSenderThread sender;

    /** The current text synthesizer. */
    private TextSynthesizedOutput textOutput;

    /** Registered call control listeners. */
    private final Collection<TelephonyListener> listener;

    /** Messages that are not acknowledged by the client. */
    private final Map<Integer, PendingMessage> pendingMessages;

    /** <code>true</code> if a notification about a hangup was already sent. */
    private boolean sentHungup;

    /**
     * Constructs a new object.
     */
    public TextTelephony() {
        listener = new java.util.ArrayList<TelephonyListener>();
        pendingMessages = new java.util.HashMap<Integer, PendingMessage>();
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SynthesizedOutput output,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        if (sentHungup) {
            throw new NoresourceError("connection disconnected");
        }
        if (!(output instanceof TextSynthesizedOutput)) {
            throw new IOException("output does not deliver text!");
        }
        textOutput = (TextSynthesizedOutput) output;

        // Retrieves the next message asynchronously.
        final Thread thread = new Thread() {
            @Override
            public void run() {
                final SpeakableText speakable = textOutput.getNextText();
                synchronized (pendingMessages) {
                    firePlayStarted();
                    if (sender != null) {
                        sender.sendData(speakable);
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * Adds the given sequence number to the list of pending messages.
     * @param sequenceNumber the sequence number to add.
     * @param message the sent message
     */
    void addPendingMessage(final int sequenceNumber,
            final PendingMessage message) {
        if (sequenceNumber <= 0) {
            return;
        }
        synchronized (pendingMessages) {
            final Integer object = new Integer(sequenceNumber);
            pendingMessages.put(object, message);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("added pending message " + sequenceNumber);
            }
        }
    }

    /**
     * Removes the given sequence number from the list of pending messages.
     * @param sequenceNumber the sequence number to add.
     * @return <code>true</code> if the message has been removed.
     */
    boolean removePendingMessage(final int sequenceNumber) {
        final boolean removed;
        synchronized (pendingMessages) {
            final Integer object = new Integer(sequenceNumber);
            final PendingMessage pending = pendingMessages.remove(object);
            removed = pending != null;
            if (removed) {
                final SpeakableText speakable = pending.getSpeakable();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("removed pending message " + sequenceNumber);
                }
                textOutput.checkEmptyQueue(speakable);
            }
        }
        firePlayStopped();
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        firePlayStopped();
    }

    /**
     * Notifies all listeners that play has started.
     */
    private void firePlayStarted() {
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.PLAY_STARTED);
        fireTelephonyEvent(event);
    }

    /**
     * Notifies all listeners that play has stopped.
     */
    private void firePlayStopped() {
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.PLAY_STOPPED);
        fireTelephonyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void record(final SpokenInput input,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        if (sentHungup) {
            throw new NoresourceError("connection disconnected");
        }
        if (!(input instanceof TextSpokenInput)) {
            throw new IOException("input does not support texts!");
        }
        fireRecordStarted();
        final TextSpokenInput textInput = (TextSpokenInput) input;
        receiver.setSpokenInput(textInput);

    }

    /**
     * Notification of the sender thread that the data has been transferred.
     */
    void recordStopped() {
        if (receiver != null) {
            receiver.setSpokenInput(null);
        }
        fireRecordStopped();
    }

    /**
     * {@inheritDoc}
     *
     * @return <code>null</code> since we do not support audio recordings.
     */
    public AudioFormat getRecordingAudioFormat() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void startRecording(final SpokenInput input,
            final OutputStream stream, final Map<String, String> parameters)
            throws NoresourceError, IOException {
        throw new NoresourceError(
                "recording to output streams is currently not supported");
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording() throws NoresourceError {
        fireRecordStopped();
    }

    /**
     * Notifies all listeners that play has started.
     */
    private void fireRecordStarted() {
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.RECORD_STARTED);
        fireTelephonyEvent(event);
    }

    /**
     * Notifies all listeners that play has stopped.
     */
    private void fireRecordStopped() {
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.RECORD_STOPPED);
        fireTelephonyEvent(event);
    }

    /**
     * Notifies all listeners about an unexpected disconnect.
     * @since 0.7
     */
    synchronized void fireHungup() {
        if (sentHungup) {
            return;
        }
        sentHungup = true;
        if (textOutput != null) {
            textOutput.disconnected();
        }
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.HUNGUP);
        fireTelephonyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final String dest) throws NoresourceError {
        throw new NoresourceError("transfer is not supported!");
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return TextConnectionInformation.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        synchronized (pendingMessages) {
            return (sender != null && sender.isSending())
            || !pendingMessages.isEmpty()
            || (receiver != null && receiver.isRecording());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        listener.clear();
        if (receiver != null) {
            receiver.interrupt();
            receiver = null;
        }
        if (sender != null) {
            sender.interrupt();
            sender = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error closing socket", e);
                }
            }
            socket = null;
        }
        pendingMessages.clear();
        sentHungup = false;
        textOutput = null;
    }

    /**
     * Opens a connection to a server socket on the client side.
     * @param client connection info
     * @return created socket
     * @throws IOException
     *         if the connection could not be established
     * @since 0.7.3
     */
    private Socket openConnection(final TextConnectionInformation client)
        throws IOException {
        final InetAddress address = client.getAddress();
        final int port = client.getPort();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting to '" + client.getCallingDevice()
                    + "'...");
        }

        final SocketAddress socketAddress =
            new InetSocketAddress(address, port);
        final Socket endpoint = new Socket();
        endpoint.connect(socketAddress, MAX_TIMEOUT_CONNECT);
        return endpoint;
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation info) throws IOException {
        if (info instanceof TextConnectionInformation) {
            final TextConnectionInformation textClient = (TextConnectionInformation) info;
            socket = openConnection(textClient);
        } else if (info instanceof ConnectedTextConnectionInformation) {
            final ConnectedTextConnectionInformation textClient =
                (ConnectedTextConnectionInformation) info;
            socket = textClient.getSocket();
        } else {
            throw new IOException("Unsupported connection information '"
                    + info + "'");
        }
        receiver = new TextReceiverThread(socket, this);
        receiver.start();
        try {
            receiver.waitStarted();
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
        sender = new TextSenderThread(socket, this);
        sender.start();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client) {
        if (LOGGER.isDebugEnabled()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("disconnecting from '" + client.getCallingDevice()
                        + "'...");
            }
        }

        if (!sentHungup && sender.isAlive()) {
            sender.sendBye();
            try {
                sender.join(3000);
                Thread.sleep(300);
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("join interrupted", e);
                }
            } finally {
                if (sender.isAlive()) {
                    sender.interrupt();
                }
                sender = null;
            }
        }

        if (receiver != null) {
            receiver.interrupt();
            receiver = null;
        }
        try {
            socket.close();
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error disconnecting", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("disconnected");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final TelephonyListener callListener) {
        synchronized (listener) {
            listener.add(callListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(
            final TelephonyListener callListener) {
            synchronized (listener) {
                listener.remove(callListener);
            }
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     */
    private void fireTelephonyEvent(final TelephonyEvent event) {
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            for (TelephonyListener current : copy) {
                switch(event.getEvent()) {
                case TelephonyEvent.ANSWERED:
                    current.telephonyCallAnswered(event);
                    break;
                case TelephonyEvent.HUNGUP:
                    current.telephonyCallHungup(event);
                default:
                    current.telephonyMediaEvent(event);
                }
            }
        }
    }
}
