/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;

/**
 * Dummy implementation of a {@link Telephony} resource.
 *
 * <p>
 * This implementation of a {@link Telephony} resource can be used, if there
 * is no telephony support or if the {@link SpokenInput} and
 * {@link SynthesizedOutput} implementations are able to produced communicate
 * directly with the user.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.5.5
 */
public final class DummyTelephonySupport
    implements Telephony, ObservableTelephony {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(DummyTelephonySupport.class);

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
    public DummyTelephonySupport() {
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
        return "dummy";
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
    public void connect(final ConnectionInformation client)
        throws IOException {
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.ANSWERED);
            for (TelephonyListener current : copy) {
                current.telephonyCallAnswered(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client) {
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.HUNGUP);
            for (TelephonyListener current : copy) {
                current.telephonyCallHungup(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SynthesizedOutput output,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.PLAY_STARTED);
            for (TelephonyListener current : copy) {
                current.telephonyMediaEvent(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        if (!busy) {
            return;
        }
        busy = false;
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.PLAY_STOPPED);
            for (TelephonyListener current : copy) {
                current.telephonyMediaEvent(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void record(final SpokenInput input,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.RECORD_STARTED);
            for (TelephonyListener current : copy) {
                current.telephonyMediaEvent(event);
            }
        }
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
    public void startRecording(final SpokenInput input,
            final OutputStream stream,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
        recording = new RecordingThread(stream, RECORDING_AUDIO_FORMAT);
        recording.start();
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.RECORD_STARTED);
            for (TelephonyListener current : copy) {
                current.telephonyMediaEvent(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording() throws NoresourceError {
        if (!busy) {
            return;
        }
        busy = false;
        if (recording != null) {
            recording.stopRecording();
            recording = null;
        }
        synchronized (listener) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listener);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.RECORD_STOPPED);
            for (TelephonyListener current : copy) {
                current.telephonyMediaEvent(event);
            }
        }
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
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return busy;
    }
}

