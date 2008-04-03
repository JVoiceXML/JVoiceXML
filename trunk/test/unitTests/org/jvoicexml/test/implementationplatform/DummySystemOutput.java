/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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
package org.jvoicexml.test.implementationplatform;

import java.util.Collection;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSystemOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputProvider;
import org.jvoicexml.implementation.SystemOutputListener;

/**
 * This class provides a dummy implementation of a {@link SystemOutput} for
 * testing purposes.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class DummySystemOutput implements SystemOutput,
    ObservableSystemOutput, SynthesizedOutputProvider {
    /** Registered output listener. */
    private final Collection<SystemOutputListener> listener;

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
        listener = new java.util.ArrayList<SystemOutputListener>();
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
        synchronized (listener) {
            for (SystemOutputListener current : listener) {
                current.outputStarted(speakable);
            }
        }
    }

    /**
     * Simulates the end of an output.
     */
    public void outputEnded() {
        synchronized (listener) {
            for (SystemOutputListener current : listener) {
                current.outputEnded(speakable);
            }
        }
        speakable = null;
        synchronized (listener) {
            for (SystemOutputListener current : listener) {
                current.outputQueueEmpty();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addSystemOutputListener(
            final SystemOutputListener outputListener) {
        synchronized (listener) {
            listener.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeSystemOutputListener(
            final SystemOutputListener outputListener) {
        synchronized (listener) {
            listener.remove(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOutput getSynthesizedOutput() throws NoresourceError {
        return output;
    }
}
