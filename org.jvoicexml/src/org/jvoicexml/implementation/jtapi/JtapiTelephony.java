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

package org.jvoicexml.implementation.jtapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.callmanager.jtapi.JVoiceXmlTerminal;
import org.jvoicexml.callmanager.jtapi.JtapiRemoteClient;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputProvider;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputProvider;
import org.jvoicexml.implementation.Telephony;

/**
 * JTAPI based implementation of a {@link Telephony}.
 *
 * <p>
 * Audio output and user input is achieved via URIs.
 * </p>
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public final class JtapiTelephony implements Telephony,
        ObservableTelephony, TelephonyListener {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(JtapiTelephony.class);

    /** Listener to this call control. */
    private final Collection<TelephonyListener> callControlListeners;

    /** The JTAPI connection. */
    private JVoiceXmlTerminal terminal;

    /**
     * Constructs a new object.
     */
    public JtapiTelephony() {
        callControlListeners = new java.util.ArrayList<TelephonyListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final TelephonyListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final TelephonyListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SystemOutput output,
            final Map<String, String> parameters)
        throws NoresourceError, IOException {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }
        if (output instanceof SynthesizedOutputProvider) {
            final SynthesizedOutputProvider provider =
                (SynthesizedOutputProvider) output;
            final SynthesizedOutput snthesizer =
                provider.getSynthesizedOutput();
            final URI uri = snthesizer.getUriForNextSynthesisizedOutput();

            terminal.play(uri, parameters);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void record(final UserInput input,
            final Map<String, String> parameters)
        throws NoresourceError, IOException {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        if (input instanceof SpokenInputProvider) {
            final SpokenInputProvider provider = (SpokenInputProvider) input;
            final SpokenInput spokenInput = provider.getSpokenInput();
            final URI uri = spokenInput.getUriForNextSpokenInput();
            // TODO Do the actual recording.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("recording to URI '" + uri + "'...");
            }
            // TODO Move the code from the FIA to here.
            terminal.record(uri, parameters);
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
    public void startRecording(final UserInput input, final OutputStream stream,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording() throws NoresourceError {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        terminal.stopRecord();
    }

    /**
     * Inform the {@link TelephonyListener} about an answered event.
     */
    protected void fireAnswerEvent() {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.ANSWERED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallAnswered(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a play started event.
     */
    protected void firePlayEvent() {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.PLAY_STARTED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a play stopped event.
     */
    protected void fireplayStoppedEvent() {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.PLAY_STOPPED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a record started event.
     */
    protected void fireRecordStartedEvent() {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.RECORD_STARTED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a record stopped event.
     */
    protected void fireRecordStoppedEvent() {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.RECORD_STOPPED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a hangup event.
     */
    protected void firehangedUpEvent() {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.HUNGUP);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallHungup(event);
        }
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
        return "jtapi";
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
        callControlListeners.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        final JtapiRemoteClient remote = (JtapiRemoteClient) client;
        terminal = remote.getTerminal();
        terminal.addListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        final JtapiRemoteClient remote = (JtapiRemoteClient) client;
        terminal = remote.getTerminal();
        terminal.removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        if (terminal == null) {
          throw new NoresourceError("No active telephony connection!");
      }

      terminal.stopPlay();
    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final String dest) throws NoresourceError {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        terminal.transfer(dest);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        if (terminal == null) {
            return false;
        }

        return terminal.isBusy();
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallAnswered(final TelephonyEvent event) {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallAnswered(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallHungup(final TelephonyEvent event) {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallAnswered(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyMediaEvent(final TelephonyEvent event) {
        final Collection<TelephonyListener> tmp =
            new java.util.ArrayList<TelephonyListener>(callControlListeners);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallAnswered(event);
        }
    }
}
