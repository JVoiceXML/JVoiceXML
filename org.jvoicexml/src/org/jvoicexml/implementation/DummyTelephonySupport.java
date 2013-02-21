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

package org.jvoicexml.implementation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.NoresourceError;

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

    /** Registered output listener. */
    private final Collection<TelephonyListener> listener;

    /** Flag if this device is busy. */
    private boolean busy;

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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivated telephony");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client)
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
    public void disconnect(final RemoteClient client) {
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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void startRecording(final SpokenInput input,
            final OutputStream stream,
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
    public void stopRecording() throws NoresourceError {
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

