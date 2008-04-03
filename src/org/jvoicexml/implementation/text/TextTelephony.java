/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.client.text.TextRemoteClient;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CallControlListener;
import org.jvoicexml.implementation.ObservableCallControl;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputProvider;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputProvider;
import org.jvoicexml.implementation.Telephony;

/**
 * Text based frontend.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class TextTelephony implements Telephony, ObservableCallControl {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextTelephony.class);

    /** The connection to the client. */
    private Socket socket;

    /** Receiver for messages from the client. */
    private TextReceiverThread receiver;

    /** Sender for messages to the client. */
    private TextSenderThread sender;

    /** Registered call control listeners. */
    private final Collection<CallControlListener> listener;

    /** Messages that are not acknowledged by the client. */
    private final Collection<Integer> pendingMessages;

    /**
     * Constructs a new object.
     */
    public TextTelephony() {
        listener = new java.util.ArrayList<CallControlListener>();
        pendingMessages = new java.util.ArrayList<Integer>();
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SystemOutput output,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        final SynthesizedOutput synthesizedOutput;
        if (output instanceof SynthesizedOutputProvider) {
            SynthesizedOutputProvider provider =
                (SynthesizedOutputProvider) output;
            synthesizedOutput = provider.getSynthesizedOutput();
        } else {
            synthesizedOutput = null;
        }
        if (!(synthesizedOutput instanceof TextSynthesizedOutput)) {
            throw new IOException("output does not deliver text!");
        }
        final TextSynthesizedOutput textOutput =
            (TextSynthesizedOutput) synthesizedOutput;
        final SpeakableText speakable = textOutput.getNextText();
        firePlayStarted();
        sender.sendData(speakable);
    }

    /**
     * Adds the given sequence number to the list of pending messages.
     * @param sequenceNumber the sequence number to add.
     */
    void addPendingMessage(final int sequenceNumber) {
        if (sequenceNumber <= 0) {
            return;
        }
        synchronized (pendingMessages) {
            final Integer object = new Integer(sequenceNumber);
            pendingMessages.add(object);
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
            removed = pendingMessages.remove(object);
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
        synchronized (listener) {
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.playStarted();
            }
        }
    }

    /**
     * Notifies all listeners that play has stopped.
     */
    private void firePlayStopped() {
        synchronized (listener) {
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.playStopped();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void record(final UserInput input,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        final SpokenInput spokenInput;
        if (input instanceof SpokenInputProvider) {
            SpokenInputProvider provider =
                (SpokenInputProvider) input;
            spokenInput = provider.getSpokenInput();
        } else {
            spokenInput = null;
        }
        if (!(spokenInput instanceof TextSpokenInput)) {
            throw new IOException("input does not support texts!");
        }
        fireRecordStarted();
        final TextSpokenInput textInput = (TextSpokenInput) spokenInput;
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
    public void startRecording(final UserInput input, final OutputStream stream,
            final Map<String, String> parameters)
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
        synchronized (listener) {
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.recordStarted();
            }
        }
    }

    /**
     * Notifies all listeners that play has stopped.
     */
    private void fireRecordStopped() {
        synchronized (listener) {
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.recordStopped();
            }
        }
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
        return TextRemoteClient.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return sender.isSending() || !pendingMessages.isEmpty()
            || receiver.isRecording();
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
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        final TextRemoteClient textClient = (TextRemoteClient) client;
        final InetAddress address = textClient.getAddress();
        final int port = textClient.getPort();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting to '" + address + ":" + port + "...");
        }

        final SocketAddress socketAddress =
            new InetSocketAddress(address, port);
        socket = new Socket();
        socket.connect(socketAddress);

        receiver = new TextReceiverThread(socket, this);
        receiver.start();
        synchronized (receiver) {
            try {
                receiver.wait();
            } catch (InterruptedException e) {
               throw new IOException(e.getMessage());
            }
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
    public void disconnect(final RemoteClient client) {
        if (LOGGER.isDebugEnabled()) {
            final TextRemoteClient textClient = (TextRemoteClient) client;
            final InetAddress address = textClient.getAddress();
            final int port = textClient.getPort();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("disconnecting from '" + address + ":" + port
                        + "...");
            }
        }

        sender.sendBye();

        if (receiver != null) {
            receiver.interrupt();
            receiver = null;
        }
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.debug("error disconnecting from remote client", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("disconnected");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addCallControlListener(final CallControlListener callListener) {
        synchronized (listener) {
            listener.add(callListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeCallControlListener(
            final CallControlListener callListener) {
            synchronized (listener) {
                listener.remove(callListener);
            }
    }
}
