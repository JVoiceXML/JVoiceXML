/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.jsapi20;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.EngineMode;
import javax.speech.EngineStateException;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableException;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerEvent;
import javax.speech.synthesis.SynthesizerListener;
import javax.speech.synthesis.SynthesizerMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.implementation.MarkerReachedEvent;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.AudioSource;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.jsapi2.synthesis.BaseSynthesizerAudioManager;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.jvoicexml.xml.vxml.PriorityType;

/**
 * Audio output that uses the JSAPI 2.0 to address the TTS engine.
 * 
 * <p>
 * Handle all JSAPI calls to the TTS engine to make JSAPI transparent to the
 * interpreter.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 * @author Renato Cassaca
 * @since 0.6
 */
public final class Jsapi20SynthesizedOutput
        implements SynthesizedOutput, SpeakableListener, SynthesizerListener,
        AudioSource {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(Jsapi20SynthesizedOutput.class);

    /** Number of msec to wait when waiting for an empty queue. */
    private static final int WAIT_EMPTY_TIMEINTERVALL = 300;

    /** The used synthesizer. */
    private Synthesizer synthesizer;

    /** The default synthesizer mode descriptor. */
    private final SynthesizerMode desc;

    /** The system output listener. */
    private final Collection<SynthesizedOutputListener> listeners;

    /** Type of this resources. */
    private String type;

    /** The media locator to use. */
    private String mediaLocator;

    /** Object lock for an empty queue. */
    private final Object emptyLock;

    /** Queued speakables. */
    private final List<SpeakableText> queuedSpeakables;

    /** A map of queued speakables to their id returned in speak requests. */
    private final Map<SpeakableText, Integer> queuedIds;

    /** <code>true</code> if the synthesizer supports SSML. */
    private boolean supportsMarkup;

    /** The current session id. */
    private SessionIdentifier sessionId;

    /**
     * Constructs a new audio output.
     * 
     * @param defaultDescriptor
     *            the default synthesizer mode descriptor.
     * @param mediaLocatorFactory
     *            factory to create a sink media locator
     */
    public Jsapi20SynthesizedOutput(final SynthesizerMode defaultDescriptor,
            final OutputMediaLocatorFactory mediaLocatorFactory) {
        desc = defaultDescriptor;
        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        queuedSpeakables = new java.util.LinkedList<SpeakableText>();
        queuedIds = new java.util.HashMap<SpeakableText, Integer>();
        emptyLock = new Object();
    }

    /**
     * Sets the media locator.
     * 
     * @param locator
     *            the media locator to use.
     * @since 0.7
     */
    public void setMediaLocator(final URI locator) {
        if (locator != null) {
            mediaLocator = locator.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
        try {
            synthesizer = (Synthesizer) EngineManager.createEngine(desc);
            if (synthesizer == null) {
                throw new NoresourceError(
                        "no synthesizer found matching " + desc);
            }
            LOGGER.info("allocating JSAPI 2.0 synthesizer...");
            if (mediaLocator != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("using media locator '" + mediaLocator + "'");
                }
                final AudioManager manager = synthesizer.getAudioManager();
                manager.setMediaLocator(mediaLocator);
            }
            synthesizer.allocate();
            synthesizer.setSpeakableMask(SpeakableEvent.DEFAULT_MASK
                    | SpeakableEvent.SPEAKABLE_FAILED);
            synthesizer.addSpeakableListener(this);
            synthesizer.addSynthesizerListener(this);
            synthesizer.waitEngineState(Synthesizer.ALLOCATED);
        } catch (EngineStateException ex) {
            throw new NoresourceError("Error allocating synthesizer", ex);
        } catch (EngineException ex) {
            throw new NoresourceError("Error allocating synthesizer", ex);
        } catch (AudioException ex) {
            throw new NoresourceError("Error allocating synthesizer", ex);
        } catch (IllegalArgumentException ex) {
            throw new NoresourceError("Error allocating synthesizer", ex);
        } catch (IllegalStateException ex) {
            throw new NoresourceError("Error allocating synthesizer", ex);
        } catch (InterruptedException ex) {
            throw new NoresourceError("Error allocating synthesizer", ex);
        }

        final EngineMode mode = synthesizer.getEngineMode();
        final Boolean markupSupport = mode.getSupportsMarkup();
        supportsMarkup = markupSupport != Boolean.FALSE;

        LOGGER.info("...JSAPI 2.0 synthesizer allocated");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot deallocate");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio output...");
        }

        waitQueueEmpty();
        synchronized (queuedIds) {
            queuedIds.clear();
        }

        LOGGER.info("deallocating  JSAPI 2.0 synthesizer...");

        try {
            synthesizer.deallocate();
        } catch (AudioException ex) {
            LOGGER.error("Error deallocating synthesizer", ex);
            return;
        } catch (EngineStateException ex) {
            LOGGER.error("Error deallocating synthesizer", ex);
            return;
        } catch (EngineException ee) {
            LOGGER.warn("error deallocating synthesizer", ee);
        } finally {
            synthesizer = null;
        }
        LOGGER.info("...JSAPI 2.0 synthesizer deallocated");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("audio output closed");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SynthesizedOutputListener outputListener) {
        synchronized (listeners) {
            listeners.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SynthesizedOutputListener outputListener) {
        synchronized (listeners) {
            listeners.remove(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Checks the type of the given speakable and forwards it either as for SSML
     * output or for plain text output.
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final SessionIdentifier sessId, final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        // Remember the new session id.
        sessionId = sessId;

        synchronized (queuedSpeakables) {
            final PriorityType priority = speakable.getPriority();
            if (priority.equals(PriorityType.CLEAR)) {
                final Collection<SpeakableText> pendingSpeakables =
                        queuedIds.keySet();
                queuedSpeakables.retainAll(pendingSpeakables);
            }
            if (priority.equals(PriorityType.CLEAR)) {
                queuedSpeakables.clear();
                queuedSpeakables.add(speakable);
            } else if (priority.equals(PriorityType.PREPEND)) {
                queuedSpeakables.add(0, speakable);
            } else {
                queuedSpeakables.add(speakable);
            }
        }
        LOGGER.info("queued speakable: " + speakable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPrompts(final SessionIdentifier id,
            final DocumentServer server, final CallControlProperties callProps)
                    throws BadFetchError, NoresourceError,
                        ConnectionDisconnectHangupEvent {
        final Runnable runnable = new Runnable() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                try {
                    processNextSpeakable();
                } catch (NoresourceError e) {
                    notifyError(e);
                } catch (BadFetchError e) {
                    notifyError(e);
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Queues the speakable SSML formatted text.
     * 
     * @param ssmlText
     *            SSML formatted text.
     * @exception NoresourceError
     *                The output resource is not available.
     * @exception BadFetchError
     *                Error reading from the <code>AudioStream</code>.
     */
    private void speakSSML(final SpeakableSsmlText ssmlText)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        if (!supportsMarkup) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("synthesizer does not support markup."
                        + " reducing to plain text");
            }
            speakPlaintext(ssmlText);
            return;
        }
        final SsmlDocument document = ssmlText.getDocument();
        final String doc = document.toString();
        LOGGER.info("speaking " + doc);
        try {
            synthesizer.resume();
            int id = synthesizer.speakMarkup(doc, null);
            synchronized (queuedIds) {
                queuedIds.put(ssmlText, id);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("queued id '" + id + "'");
            }
        } catch (IllegalArgumentException iae) {
            throw new BadFetchError(iae);
        } catch (EngineStateException ese) {
            throw new BadFetchError(ese);
        } catch (SpeakableException se) {
            throw new BadFetchError(se);
        }
    }

    /**
     * Processes the next speakable in the queue.
     * 
     * @throws NoresourceError
     *             error processing the speakable.
     * @throws BadFetchError
     *             error processing the speakable.
     * @since 0.7.1
     */
    private void processNextSpeakable() throws NoresourceError, BadFetchError {
        // Check if there are more speakables to process
        final SpeakableText speakable;
        synchronized (queuedSpeakables) {
            if (queuedSpeakables.isEmpty()) {
                LOGGER.info("no more speakables to process");
                fireQueueEmpty();
                synchronized (emptyLock) {
                    emptyLock.notifyAll();
                }
                return;
            }
            speakable = queuedSpeakables.get(0);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("processing next speakable: " + speakable);
        }

        // Actually process the next speakable
        if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            speakSSML(ssml);
        } else {
            LOGGER.warn("unsupported speakable: " + speakable);
        }
    }

    /**
     * Notifies all listeners that output has started.
     * 
     * @param speakable
     *            the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputStartedEvent(this,
                sessionId, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                    new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that the given marker has been reached.
     * 
     * @param mark
     *            the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        final SynthesizedOutputEvent event = new MarkerReachedEvent(this,
                sessionId, mark);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                    new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output has started.
     * 
     * @param speakable
     *            the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputEndedEvent(this,
                sessionId, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                    new java.util.ArrayList<SynthesizedOutputListener>(
                            listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this,
                sessionId);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                    new java.util.ArrayList<SynthesizedOutputListener>(
                            listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Speaks a plain text string.
     * 
     * @param speakable
     *            speakable containing plain text to be spoken.
     * @exception NoresourceError
     *                No synthesizer allocated.
     * @exception BadFetchError
     *                Synthesizer in wrong state.
     */
    private void speakPlaintext(final SpeakableSsmlText speakable)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }
        final SsmlDocument ssml = speakable.getDocument();
        final Speak speak = ssml.getSpeak();
        final String text = speak.getTextContent();
        LOGGER.info("speaking '" + text + "'...");
        try {
            synthesizer.resume();
            int id = synthesizer.speak(text, null);
            synchronized (queuedIds) {
                queuedIds.put(speakable, id);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("queued id '" + id + "'");
            }
        } catch (EngineStateException ese) {
            throw new BadFetchError(ese);
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
    public void cancelOutput(final BargeInType bargeInType)
            throws NoresourceError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot cancel output");
        }
        synchronized (queuedSpeakables) {
            if (queuedSpeakables.isEmpty()) {
                return;
            }
            final SpeakableText curent = queuedSpeakables.get(0);
            if (!curent.isBargeInEnabled(bargeInType)) {
                return;
            }

            final Collection<SpeakableText> skipped =
                    new java.util.ArrayList<SpeakableText>();
            for (SpeakableText speakable : queuedSpeakables) {
                if (speakable.isBargeInEnabled(bargeInType)) {
                    skipped.add(speakable);
                } else {
                    // Stop iterating after the first non-bargein speakable
                    // has been detected
                    break;
                }
            }

            queuedSpeakables.removeAll(skipped);
            for (SpeakableText speakable : skipped) {
                final Integer id;
                synchronized (queuedIds) {
                    id = queuedIds.get(speakable);
                }
                if (id != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                                "cancelling queued id '" + id.intValue() + "'");
                    }
                    synthesizer.cancel(id.intValue());
                }
            }
        }

        if (queuedSpeakables.isEmpty()) {
            fireQueueEmpty();
            synchronized (emptyLock) {
                emptyLock.notifyAll();
            }
        } else {
            final Runnable runnable = new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    try {
                        processNextSpeakable();
                    } catch (NoresourceError e) {
                        notifyError(e);
                    } catch (BadFetchError e) {
                        notifyError(e);
                    }
                }
            };
            final Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
        synchronized (queuedSpeakables) {
            if (queuedSpeakables.isEmpty()) {
                return;
            }
            final SpeakableText speakable = queuedSpeakables.get(0);
            if (!speakable.isBargeInEnabled(BargeInType.SPEECH)
                    && !speakable.isBargeInEnabled(BargeInType.HOTWORD)) {
                return;
            }
        }
        waitQueueEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitQueueEmpty() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for empty queue...");
        }
        while (!queuedSpeakables.isEmpty()) {
            synchronized (emptyLock) {
                try {
                    emptyLock.wait(WAIT_EMPTY_TIMEINTERVALL);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...queue emptied");
        }
    }

    /**
     * A mark in an SSML output has been reached.
     * 
     * @param mark
     *            Name of the mark.
     */
    public void reachedMark(final String mark) {
        if (listeners == null) {
            return;
        }

        fireMarkerReached(mark);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() throws NoresourceError {
        synthesizer.resume();
        try {
            synthesizer.waitEngineState(Engine.RESUMED);
        } catch (IllegalArgumentException | IllegalStateException
                | InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    @Override
    public AudioFormat getAudioFormat() {
        final AudioManager manager = synthesizer.getAudioManager();
        final BaseSynthesizerAudioManager baseAudioManager =
                (BaseSynthesizerAudioManager) manager;
        return baseAudioManager.getTargetAudioFormat();
    }

    @Override
    public void setOutputStream(final OutputStream out) {
//        final AudioManager manager = synthesizer.getAudioManager();
//        try {
//            manager.audioStop();
//            manager.setMediaLocator(null, out);
//            manager.audioStart();
//        } catch (AudioException | IllegalStateException
//                | IllegalArgumentException | SecurityException e) {
//            LOGGER.error(e.getMessage(), e);
//        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating output...");
        }
        queuedSpeakables.clear();
        listeners.clear();
        synthesizer.pause();
        // TODO fix the pause in the JSAPI20 implementation
//        try {
//            synthesizer.waitEngineState(Engine.PAUSED);
//        } catch (IllegalArgumentException | IllegalStateException
//                | InterruptedException e) {
//            throw new NoresourceError(e.getMessage(), e);
//        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...passivated output");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation info) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation info) {
        sessionId = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this resource.
     * 
     * @param resourceType
     *            type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        synchronized (queuedSpeakables) {
            return !queuedSpeakables.isEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void speakableUpdate(final SpeakableEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speakable updated: " + event);
        }
        final int id = event.getId();
        SpeakableText speakable = null;
        if (id == SpeakableEvent.SPEAKABLE_STARTED) {
            synchronized (queuedSpeakables) {
                speakable = queuedSpeakables.get(0);
            }
            fireOutputStarted(speakable);
        } else if (id == SpeakableEvent.SPEAKABLE_ENDED) {
            synchronized (queuedSpeakables) {
                speakable = queuedSpeakables.remove(0);
                final Integer queueId;
                synchronized (queuedIds) {
                    queueId = queuedIds.remove(speakable);
                }
                if (LOGGER.isDebugEnabled() && queueId != null) {
                    LOGGER.debug(
                            "queued id '" + queueId.intValue() + "' ended ");
                }
            }

            if (speakable != null) { 
                fireOutputEnded(speakable);
            }
            try {
                processNextSpeakable();
            } catch (NoresourceError e) {
                notifyError(e);
            } catch (BadFetchError e) {
                notifyError(e);
            }
        } else if (id == SpeakableEvent.SPEAKABLE_FAILED) {
            LOGGER.warn("speakable failed: " + event);
            final SpeakableException exception = event.getSpeakableException();
            final BadFetchError error = new BadFetchError(
                    exception.getMessage(), exception);
            notifyError(error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void synthesizerUpdate(final SynthesizerEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("synthesizer updated: " + event);
        }
        final int id = event.getId();
        if (id == SynthesizerEvent.ENGINE_PAUSED) {
            synthesizer.resume();
        }
    }

    /**
     * Notifies all registered listeners about the given event.
     * 
     * @param error
     *            the error event
     * @since 0.7.4
     */
    private void notifyError(final ErrorEvent error) {
        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                    new java.util.ArrayList<SynthesizedOutputListener>();
            copy.addAll(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputError(error);
            }
        }
    }
}
