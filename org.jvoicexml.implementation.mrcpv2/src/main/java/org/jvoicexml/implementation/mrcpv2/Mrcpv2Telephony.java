/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mrcpv2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioSource;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;

/**
 * Implementation of a {@link Telephony} resource to be used for the MRCPv2
 * implementation platform.
 * @author Dirk Schnelle-Walka
 *
 * @since 0.7.9
 */
public final class Mrcpv2Telephony implements Telephony {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(Mrcpv2Telephony.class);

    /** Registered output listener. */
    private final Collection<TelephonyListener> listener;

    /** Flag if this device is busy. */
    private boolean busy;

    /** Flag if this device is active. */
    private boolean active;

    /**
     * Constructs a new object.
     */
    public Mrcpv2Telephony() {
        listener = new java.util.ArrayList<TelephonyListener>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activated telephony");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "mrcpv2";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivated telephony");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation client) throws IOException {
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
        active = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation client) {
        active = false;
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
    @Override
    public void play(final SynthesizedOutput output,
            final CallControlProperties props)
            throws IOException, NoresourceError {
        if (!active) {
            throw new NoresourceError("desktop telephony is no longer active");
        }
        if (output instanceof AudioSource) {
            final AudioSource source = (AudioSource) output;
            try {
                play(source);
            } catch (LineUnavailableException e) {
                throw new NoresourceError(e.getMessage(), e);
            }
        }
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
     * Play backs audio from the given audio source.
     * 
     * @param source
     *            the current audio source
     * @throws LineUnavailableException
     *             error opening the speaker
     * @throws IOException
     *             error starting the speaker line
     * @since 0.7.8
     */
    private void play(final AudioSource source)
            throws LineUnavailableException, IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void record(final SpokenInput input,
            final CallControlProperties props)
            throws IOException, NoresourceError {
        if (!active) {
            throw new NoresourceError("desktop telephony is no longer active");
        }
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
    @Override
    public AudioFormat getRecordingAudioFormat() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecording(final SpokenInput input,
            final OutputStream stream, final CallControlProperties props)
            throws IOException, NoresourceError {
        if (!active) {
            throw new NoresourceError("desktop telephony is no longer active");
        }
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
    @Override
    public void stopRecording() throws NoresourceError {
        if (!busy) {
            return;
        }
        busy = false;
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
    @Override
    public void transfer(final String dest) throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hangup() {
        active = false;
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
    @Override
    public void addListener(final TelephonyListener callListener) {
        synchronized (listener) {
            listener.add(callListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final TelephonyListener callListener) {
        synchronized (listener) {
            listener.add(callListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        return busy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return active;
    }
}
