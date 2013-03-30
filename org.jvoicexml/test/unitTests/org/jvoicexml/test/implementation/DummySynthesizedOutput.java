/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/test/implementation/DummySynthesizedOutput.java $
 * Version: $LastChangedRevision: 2694 $
 * Date:    $Date: 2011-06-03 04:28:55 -0500 (vie, 03 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Queue;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * This class provides a dummy {@link SynthesizedOutput} for testing
 * purposes.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2694 $
 * @since 0.6
 */
public final class DummySynthesizedOutput implements SynthesizedOutput,
    ObservableSynthesizedOutput {
    /** Registered output listener. */
    private final Collection<SynthesizedOutputListener> listener;

    /** The queued speakables. */
    private final Queue<SpeakableText> speakables;

    /** Threaded speech queue. */
    private final SpeechThread thread;

    /** The session id. */
    private String id;

    /**
     * Constructs a new object.
     */
    public DummySynthesizedOutput() {
        listener = new java.util.ArrayList<SynthesizedOutputListener>();
        speakables = new java.util.LinkedList<SpeakableText>();
        thread = new SpeechThread(this);
        thread.setDaemon(true);
        thread.start();
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
    @Override
    public void queueSpeakable(final SpeakableText speakableText,
            final String sessionId, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        id = sessionId;
        speakables.offer(speakableText);
        synchronized (thread) {
            thread.notify();
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean requiresAudioFileOutput() {
        return true;
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
    public void connect(final ConnectionInformation client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client) {
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
        return !speakables.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(
            final SynthesizedOutputListener outputListener) {
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
    public void waitQueueEmpty() {
        while (!speakables.isEmpty()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
        SpeakableText speakable;
        do {
            speakable = speakables.peek();
            if (speakable != null) {
                if (!speakable.isBargeInEnabled()) {
                    return;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
            }
        } while (speakable != null);            
    }

    private class SpeechThread extends Thread {
        /** Reference to the container. */
        private final ObservableSynthesizedOutput observable;

        /**
         * Constructs a new object.
         * @param obs reference to the container.
         */
        public SpeechThread(final ObservableSynthesizedOutput obs) {
            observable = obs;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            while(true) {
                synchronized (thread) {
                    try {
                        thread.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                while (!speakables.isEmpty()) {
                    final SpeakableText speakable = speakables.peek();
                    final SynthesizedOutputEvent start =
                        new OutputStartedEvent(observable, id, speakable);
                    fireOutputEvent(start);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    speakables.poll();
                    final SynthesizedOutputEvent end =
                        new OutputEndedEvent(observable, id, speakable);
                    fireOutputEvent(end);
                }
            }
        }
    }
}
