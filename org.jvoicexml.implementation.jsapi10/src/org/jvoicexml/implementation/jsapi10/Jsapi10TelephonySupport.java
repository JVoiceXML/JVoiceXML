/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.Telephony;

/**
 * JSAPI 1.0 implementation of a {@link Telephony} resource.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.6
 */
public final class Jsapi10TelephonySupport
    implements Telephony, ObservableTelephony {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi10TelephonySupport.class);

    /** Audio format to use for recording. */
    private static final AudioFormat RECORDING_AUDIO_FORMAT;

    /** Registered output listener. */
    private final Collection<TelephonyListener> listener;

    /** Flag if this device is busy. */
    private boolean busy;


    /** Asynchronous recording of audio. */
    private RecordingThread recording;

    static {
        final AudioFormat.Encoding encoding =
                new AudioFormat.Encoding("PCM_SIGNED");
        RECORDING_AUDIO_FORMAT =
                new AudioFormat(encoding, ((float) 8000.0), 16, 1, 2,
                ((float) 8000.0), false);
    }

    /**
     * Constructs a new object.
     */
    public Jsapi10TelephonySupport() {
        listener = new java.util.ArrayList<TelephonyListener>();
    }
    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activated telephony");
        }
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
        return "jsapi10";
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
        busy = false;
        if (recording != null) {
            recording.stopRecording();
            recording = null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivated telephony");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client)
        throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SystemOutput output,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.PLAY_STARTED);
        fireTelephonyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        busy = false;
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.PLAY_STOPPED);
        fireTelephonyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void record(final UserInput input,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.RECORD_STARTED);
        fireTelephonyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void startRecording(final UserInput input, final OutputStream stream,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
        recording = new RecordingThread(stream);
        recording.start();
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.RECORD_STARTED);
        fireTelephonyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public AudioFormat getRecordingAudioFormat() {
        return RECORDING_AUDIO_FORMAT;
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording() throws NoresourceError {
        busy = false;
        if (recording != null) {
            recording.stopRecording();
            recording = null;
        }
        final TelephonyEvent event =
            new TelephonyEvent(this, TelephonyEvent.RECORD_STOPPED);
        fireTelephonyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final String dest) throws NoresourceError {
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
            listener.add(callListener);
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

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return busy;
    }
}
