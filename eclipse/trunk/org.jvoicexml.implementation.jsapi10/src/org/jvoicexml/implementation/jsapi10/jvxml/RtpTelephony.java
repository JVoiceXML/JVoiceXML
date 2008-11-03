/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.client.rtp.RtpConfiguration;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputProvider;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputProvider;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.jsapi10.StreamableSpokenInput;
import org.jvoicexml.implementation.jsapi10.StreamableSynthesizedOutput;

/**
 * RTP frontend for the JSAPI 1.0 layer.
 *
 * <p>
 * This class does not offer any telephony support but streaming
 * capablities with RTP technology for other clients, e.g. a console.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class RtpTelephony implements Telephony, ObservableTelephony {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RtpTelephony.class);

    /** Delay in msec before ending a play. */
    private static final int DELAY = 2000;

    /** Size of the send buffer. */
    private static final int SEND_BUFFER_SIZE = 1024;

    /** RTP server. */
    private final RtpServer server;

    /** Registered call control listeners. */
    private final Collection<TelephonyListener> listener;

    /** Set to <code>true</code> if this device is playing back. */
    private boolean playing;

    /** Set to <code>true</code> if this device is recording. */
    private boolean recording;

    /**
     * Constructs a new object.
     */
    public RtpTelephony() {
        server = new RtpServer();
        listener = new java.util.ArrayList<TelephonyListener>();
    }
    /**
     * {@inheritDoc}
     */
    public synchronized void play(final SystemOutput output,
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
        if (!(synthesizedOutput instanceof StreamableSynthesizedOutput)) {
            throw new IOException("output does not support streams!");
        }
        playing = true;
        final StreamableSynthesizedOutput streamable =
            (StreamableSynthesizedOutput) synthesizedOutput;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("playing output...");
        }
        firePlayStarted();
        sendSynthesizerOutput(streamable);
        // TODO Replace this by a timing solution.
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("delay interrupted", e);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("... done playing output");
        }
        playing = false;
        firePlayStopped();
    }
    /**
     * Sends the output of the synthesizer to the participant.
     * @param streamable the streamable synthesized output.
     * @throws IOException
     *         Error reading from the synthesizer.
     */
    private void sendSynthesizerOutput(
            final StreamableSynthesizedOutput streamable) throws IOException {
        final byte[] buffer = new byte[SEND_BUFFER_SIZE];
        int num = 0;
        do {
            num = streamable.readSynthesizerStream(buffer, 0, SEND_BUFFER_SIZE);
            if (num > 0) {
                server.sendData(buffer);
            }
        } while (num >= 0);
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("play stopped");
        }
        // TODO Implement the stop call.
        playing = false;
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
        if (!(spokenInput instanceof StreamableSynthesizedOutput)) {
            throw new IOException("input does not support streams!");
        }
        recording = true;
        fireRecordStarted();
        final StreamableSpokenInput streamable =
            (StreamableSpokenInput) spokenInput;
        server.setStreamableInput(streamable);
    }

    /**
     * {@inheritDoc}
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
        server.setStreamableInput(null);
        recording = false;
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
        server.close();
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi10-rtp";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("playing: " + playing + " recording: " + recording);
        }
        return playing  || recording;
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
        close();
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        RtpConfiguration rtpClient = (RtpConfiguration) client;
        server.open();
        final InetAddress address = rtpClient.getAddress();
        final int port = rtpClient.getPort();
        final int controlPort = rtpClient.getControlPort();
        server.addTarget(address, port, controlPort);


    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        server.close();
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
