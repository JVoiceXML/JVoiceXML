/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/DummyTelephonySupport.java $
 * Version: $LastChangedRevision: 726 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
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

import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CallControlListener;
import org.jvoicexml.implementation.ObservableCallControl;
import org.jvoicexml.implementation.Telephony;

/**
 * Dummy implementation of a {@link Telephony} resource.
 *
 * <p>
 * This implementation of a {@link Telephony} resource can be used, if there
 * is no telephony support.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 726 $
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public final class Jsapi10TelephonySupport
    implements Telephony, ObservableCallControl {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi10TelephonySupport.class);

    /** Registered output listener. */
    private final Collection<CallControlListener> listener;

    /** Flag if this device is busy. */
    private boolean busy;


    /** Asynchronous recording of audio. */
    private RecordingThread recording;


    /**
     * Constructs a new object.
     */
    public Jsapi10TelephonySupport() {
        listener = new java.util.ArrayList<CallControlListener>();
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
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.answered();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        synchronized (listener) {
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.hungUp();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SystemOutput output,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
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
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        busy = false;
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
        throws IOException, NoresourceError {
        busy = true;
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
     * {@inheritDoc}
     */
    public void startRecording(final UserInput input, final OutputStream stream,
            final Map<String, String> parameters)
        throws IOException, NoresourceError {
        busy = true;
        recording = new RecordingThread(stream);
        recording.start();
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
     * {@inheritDoc}
     */
    public void stopRecording() throws NoresourceError {
        busy = false;
        if (recording != null) {
            recording.interrupt();
            recording = null;
        }
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
