/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/jvxml/JVoiceXmlCallControl.java $
 * Version: $LastChangedRevision: 2913 $
 * Date:    $LastChangedDate: 2012-01-30 02:41:09 -0600 (lun, 30 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputProvider;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputProvider;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;

/**
 * Basic wrapper for {@link CallControl}. Method calls are forwarded to
 * the {@link Telephony} implementation.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2913 $
 * @since 0.6
 */
final class JVoiceXmlCallControl implements CallControl, ObservableTelephony {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(JVoiceXmlCallControl.class);

    /** The encapsulated telephony object. */
    private final Telephony telephony;

    /** Registered output listener. */
    private final Collection<TelephonyListener> listeners;

    /**
     * Constructs a new object.
     * @param tel encapsulated telephony object.
     */
    public JVoiceXmlCallControl(final Telephony tel) {
        telephony = tel;
        listeners = new java.util.ArrayList<TelephonyListener>();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation expects that the given output implements
     * {@link SynthesizedOutputProvider} to retrieve the
     * {@link SynthesizedOutput} that is needed to trigger the
     * {@link Telephony} implementation.
     * </p>
     */
    @Override
    public void play(final SystemOutput output,
            final CallControlProperties props)
            throws NoresourceError, IOException {
        if (output instanceof SynthesizedOutputProvider) {
            final SynthesizedOutputProvider provider =
                (SynthesizedOutputProvider) output;
            final SynthesizedOutput synthesizer =
                provider.getSynthesizedOutput();
            telephony.play(synthesizer, props);
        } else {
            LOGGER.warn("unable to retrieve a synthesized output from "
                    + output);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        telephony.stopPlay();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation expects that the given output implements
     * {@link SpokenInputProvider} to retrieve the
     * {@link SpokenInput} that is needed to trigger the
     * {@link Telephony} implementation.
     * </p>
     */
    @Override
    public void record(final UserInput input,
            final CallControlProperties props)
            throws NoresourceError, IOException {
        if (input instanceof SpokenInputProvider) {
            final SpokenInputProvider provider =
                (SpokenInputProvider) input;
            final SpokenInput recognizer = provider.getSpokenInput();
            telephony.record(recognizer, props);
        } else {
            LOGGER.warn("unable to retrieve a recognizer output from "
                    + input);
        }
    }

    /**
     * {@inheritDoc}
     */
    public AudioFormat getRecordingAudioFormat() {
        return telephony.getRecordingAudioFormat();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation expects that the given output implements
     * {@link SpokenInputProvider} to retrieve the
     * {@link SpokenInput} that is needed to trigger the
     * {@link Telephony} implementation.
     * </p>
     */
    @Override
    public void startRecording(final UserInput input, final OutputStream stream,
            final CallControlProperties props)
            throws NoresourceError, IOException {
        if (input instanceof SpokenInputProvider) {
            final SpokenInputProvider provider =
                (SpokenInputProvider) input;
            final SpokenInput recognizer = provider.getSpokenInput();
            telephony.startRecording(recognizer, stream, props);
        } else {
            LOGGER.warn("unable to retrieve a recognizer output from "
                    + input);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecord() throws NoresourceError {
        telephony.stopRecording();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transfer(final String dest) throws NoresourceError {
        telephony.transfer(dest);
    }

    /**
     * Retrieves the encapsulated telephony object.
     * @return the encapsulated telephony object.
     */
    public Telephony getTelephony() {
        return telephony;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final TelephonyListener listener) {
        if (telephony instanceof ObservableTelephony) {
            telephony.addListener(listener);
        }

        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final TelephonyListener listener) {
        if (telephony instanceof ObservableTelephony) {
            telephony.removeListener(listener);
        }

        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Checks if the corresponding telephony device is busy.
     * @return <code>true</code> if the telephony devices is busy.
     */
    public boolean isBusy() {
        return telephony.isBusy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hangup() {
        LOGGER.info("terminating telephony");
        if (telephony.isBusy()) {
            try {
                telephony.stopPlay();
                telephony.stopRecording();
            } catch (NoresourceError e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error stopping telephony", e);
                }
            }
        }
        synchronized (listeners) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(listeners);
            final TelephonyEvent event = new TelephonyEvent(this,
                    TelephonyEvent.HUNGUP);
            for (TelephonyListener listener : copy) {
                listener.telephonyCallHungup(event);
            }
        }
    }
}
