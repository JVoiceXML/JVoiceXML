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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.ObservableSystemOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SystemOutputListener;

/**
 * This class provides a dummy {@link SynthesizedOutput} for testing
 * purposes.
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
public final class DummySynthesizedOutput implements SynthesizedOutput,
    ObservableSystemOutput {
    /** Registered output listener. */
    private final Collection<SystemOutputListener> listener;

    /** The current speakable. */
    private SpeakableText speakable;

    /**
     * Constructs a new object.
     */
    public DummySynthesizedOutput() {
        listener = new java.util.ArrayList<SystemOutputListener>();
    }


    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void queuePlaintext(final String text) throws NoresourceError,
            BadFetchError {

    }

    /**
     * {@inheritDoc}
     */
    public void queueSpeakable(final SpeakableText speakableText,
            final boolean bargein, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        speakable = speakableText;
        synchronized (listener) {
            for (SystemOutputListener current : listener) {
                current.outputStarted(speakable);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setAudioFileOutput(final AudioFileOutput fileOutput) {
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
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return speakable != null;
    }

    /**
     * Simulates the end of an output.
     */
    public void outputEnded() {
        synchronized (listener) {
            final Collection<SystemOutputListener> copy =
                new java.util.ArrayList<SystemOutputListener>();
            copy.addAll(listener);
            for (SystemOutputListener current : copy) {
                current.outputEnded(speakable);
            }
        }
        speakable = null;
        synchronized (listener) {
            final Collection<SystemOutputListener> copy =
                new java.util.ArrayList<SystemOutputListener>();
            copy.addAll(listener);
            for (SystemOutputListener current : copy) {
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
}
