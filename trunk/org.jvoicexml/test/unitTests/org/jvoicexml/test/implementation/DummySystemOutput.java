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
package org.jvoicexml.test.implementation;

import java.util.Collection;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.implementation.SynthesizedOutputProvider;

/**
 * This class provides a dummy implementation of a {@link SystemOutput} for
 * testing purposes.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class DummySystemOutput implements SystemOutput,
    ObservableSynthesizedOutput, SynthesizedOutputProvider {
    /** Registered output listener. */
    private final Collection<SynthesizedOutputListener> listener;

    /** The current speakable. */
    private SpeakableText speakable;

    /** The encapsulated synthesized output. */
    private final SynthesizedOutput output;

    /**
     * Constructs a new object.
     */
    public DummySystemOutput() {
        this(null);
    }

    /**
     * Constructs a new object.
     * @param synthesizedOutput the encapsulated synthesized output.
     */
    public DummySystemOutput(final SynthesizedOutput synthesizedOutput) {
        listener = new java.util.ArrayList<SynthesizedOutputListener>();
        output = synthesizedOutput;
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void queueSpeakable(final SpeakableText speakableText,
            final boolean bargein, final DocumentServer documentServer)
        throws NoresourceError, BadFetchError {
        speakable = speakableText;
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.OUTPUT_STARTED, speakable);
        fireOutputEvent(event);
    }

    /**
     * Simulates the end of an output.
     */
    public void outputEnded() {
        final SynthesizedOutputEvent endedEvent =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.OUTPUT_ENDED, speakable);
        fireOutputEvent(endedEvent);
        speakable = null;
        final SynthesizedOutputEvent emptyEvent =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.QUEUE_EMPTY);
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
}
