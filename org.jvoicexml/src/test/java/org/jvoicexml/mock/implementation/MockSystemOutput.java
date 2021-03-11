/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.mock.implementation;

import java.util.Collection;

import org.jvoicexml.CallControlProperties;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * This class provides a dummy implementation of a {@link SystemOutput} for
 * testing purposes.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public final class MockSystemOutput implements SystemOutput {
    /** Registered output listener. */
    private final Collection<SynthesizedOutputListener> listener;

    /** The current speakable. */
    private SpeakableText speakable;

    /** The encapsulated synthesized output. */
    private final SynthesizedOutput output;

    /** the session id. */
    private SessionIdentifier sessionId;

    /**
     * Constructs a new object.
     */
    public MockSystemOutput() {
        this(null);
    }

    /**
     * Constructs a new object.
     * @param synthesizedOutput the encapsulated synthesized output.
     */
    public MockSystemOutput(final SynthesizedOutput synthesizedOutput) {
        listener = new java.util.ArrayList<SynthesizedOutputListener>();
        output = synthesizedOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOutput(final BargeInType type) throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueSpeakable(final SpeakableText speakableText,
            final SessionIdentifier id, final DocumentServer documentServer)
        throws NoresourceError, BadFetchError {
        speakable = speakableText;
        sessionId = id;
    }

    /**
     * Simulates the end of an output.
     */
    public void outputEnded() {
        final SynthesizedOutputEvent endedEvent =
            new OutputEndedEvent(null, sessionId, speakable);
        fireOutputEvent(endedEvent);
        speakable = null;
        final SynthesizedOutputEvent emptyEvent =
            new QueueEmptyEvent(null, sessionId);
        fireOutputEvent(emptyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(
            final SynthesizedOutputListener outputListener) {
        if (outputListener == null) {
            return;
        }
        synchronized (listener) {
            listener.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(
            final SynthesizedOutputListener outputListener) {
        synchronized (listener) {
            listener.remove(outputListener);
        }
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    private void fireOutputEvent(final SynthesizedOutputEvent event) {
        synchronized (listener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>();
            copy.addAll(listener);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOutput getSynthesizedOutput() throws NoresourceError {
        return output;
    }

    @Override
    public void playPrompts(SessionIdentifier sessionId, DocumentServer server,
            CallControlProperties callProps) throws BadFetchError,
            NoresourceError, ConnectionDisconnectHangupEvent {
        final SynthesizedOutputEvent event =
                new OutputStartedEvent(null, sessionId, speakable);
        fireOutputEvent(event);
        outputEnded();
    }
}
