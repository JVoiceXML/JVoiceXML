/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.implementation.SynthesizedOutputProvider;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Basic wrapper for {@link SystemOutput}.
 *
 * <p>
 * The {@link JVoiceXmlSystemOutput} encapsulates two external resources, the
 * {@link SynthesizedOutput} and the {@link AudioFileOutput}. Both resources
 * are obtained from a pool using the same type.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
final class JVoiceXmlSystemOutput
    implements SystemOutput, SynthesizedOutputProvider {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LogManager.getLogger(JVoiceXmlSystemOutput.class);

    /** The synthesizer output device. */
    private final SynthesizedOutput synthesizedOutput;

    /**
     * Constructs a new object.
     * @param synthesizer the synthesizer output device.
     * @param currentSession the current session.
     */
    JVoiceXmlSystemOutput(final SynthesizedOutput synthesizer,
            final Session currentSession) {
        synthesizedOutput = synthesizer;
    }

    /**
     * Retrieves the synthesized output resource.
     * @return the synthesized output resource.
     */
    public SynthesizedOutput getSynthesizedOutput() {
        return synthesizedOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final SessionIdentifier sessionId,
            final DocumentServer documentServer) throws NoresourceError,
                BadFetchError, ConnectionDisconnectHangupEvent {
        synthesizedOutput.queueSpeakable(speakable, sessionId, documentServer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPrompts(final SessionIdentifier sessionId,
            final DocumentServer server, final CallControlProperties callProps)
                    throws BadFetchError, NoresourceError,
                        ConnectionDisconnectHangupEvent {
        synthesizedOutput.playPrompts(sessionId, server, callProps);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOutput(final BargeInType type) throws NoresourceError {
        final boolean supportsBargeIn = synthesizedOutput.supportsBargeIn();
        if (!supportsBargeIn) {
            LOGGER.warn("implementation platform does not support barge-in");
            return;
        }
        synthesizedOutput.cancelOutput(type);
    }

    /**
     * Adds the given listener for output events.
     * @param listener the listener to add
     */
    public void addListener(final SynthesizedOutputListener listener) {
        synthesizedOutput.addListener(listener);
    }

    /**
     * Removes the given listener for output events.
     * @param listener the listener to remove
     */
    public void removeListener(
            final SynthesizedOutputListener listener) {
        synthesizedOutput.removeListener(listener);
    }

    /**
     * Checks if the corresponding output device is busy.
     * @return <code>true</code> if the output devices is busy.
     */
    public boolean isBusy() {
        return synthesizedOutput.isBusy();
    }
}
