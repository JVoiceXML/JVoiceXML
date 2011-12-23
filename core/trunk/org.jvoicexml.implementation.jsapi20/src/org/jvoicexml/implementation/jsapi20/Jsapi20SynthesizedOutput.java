/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Queue;

import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.EngineMode;
import javax.speech.EngineStateException;
import javax.speech.synthesis.PhoneInfo;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableException;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerEvent;
import javax.speech.synthesis.SynthesizerListener;
import javax.speech.synthesis.SynthesizerMode;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakablePhoneInfo;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesisResult;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.MarkerReachedEvent;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.OutputUpdateEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;


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
 * @version $Revision$
 * @since 0.6
 */
public final class Jsapi20SynthesizedOutput
        implements SynthesizedOutput, ObservableSynthesizedOutput,
        SpeakableListener, SynthesizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(Jsapi20SynthesizedOutput.class);

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

    /** Media locator factory to create a sink media locator. */
    private final OutputMediaLocatorFactory locatorFactory;

    /** Object lock for an empty queue. */
    private final Object emptyLock;
    
    /**
     * Flag to indicate that TTS output and audio can be canceled.
     *
     * @todo Replace this by a solution that does not cancel output without
     *       bargein, if there is mixed output.
     */
    private boolean enableBargeIn;

    /** Queued speakables. */
    private final Queue<SpeakableText> queuedSpeakables;

    /** Flag if the phone info has been posted for the current speakable. */
    private boolean hasSentPhones;

    /** <code>true</code> if the synthesizer supports SSML. */
    private boolean supportsMarkup;

    /** The current session id. */
    private String sessionId;

    /**
     * Constructs a new audio output.
     *
     * @param defaultDescriptor
     *                the default synthesizer mode descriptor.
     * @param mediaLocatorFactory
     *                factory to create a sink media locator
     */
    public Jsapi20SynthesizedOutput(final SynthesizerMode defaultDescriptor,
            final OutputMediaLocatorFactory mediaLocatorFactory) {
        desc = defaultDescriptor;
        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        queuedSpeakables = new java.util.LinkedList<SpeakableText>();
        hasSentPhones = false;
        locatorFactory = mediaLocatorFactory;
        emptyLock = new Object();
    }

    /**
     * Sets the media locator.
     * @param locator the media locator to use.
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
    public void open() throws NoresourceError {
        try {
            synthesizer = (Synthesizer) EngineManager.createEngine(desc);
            if (synthesizer == null) {
                throw new NoresourceError("no synthesizer found matching "
                        + desc);
            }
            LOGGER.info("allocating JSAPI 2.0 synthesizer...");
            if (mediaLocator != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("using media locator '" + mediaLocator
                            + "'");
                }
                final AudioManager manager = synthesizer.getAudioManager();
                manager.setMediaLocator(mediaLocator);
            }
            synthesizer.allocate();
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
    public void close() {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot deallocate");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio output...");
        }

        waitQueueEmpty();

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
    public void addListener(final SynthesizedOutputListener outputListener) {
        synchronized (listeners) {
            listeners.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
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
            final String sessId, final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        // Remember the new session id.
        sessionId = sessId;

        synchronized (queuedSpeakables) {
            queuedSpeakables.offer(speakable);
            // Do not process the speakable if there is some ongoing processing
            if (queuedSpeakables.size() > 1) {
                return;
            }
        }

        // Otherwise process the added speakable asynchronous.
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

    /**
     * Queues the speakable SSML formatted text.
     *
     * @param ssmlText
     *                SSML formatted text.
     * @exception NoresourceError
     *                    The output resource is not available.
     * @exception BadFetchError
     *                    Error reading from the <code>AudioStream</code>.
     */
    private void speakSSML(final SpeakableSsmlText ssmlText)
        throws NoresourceError,
            BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        final SsmlDocument document = ssmlText.getDocument();
        if (!supportsMarkup) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                 "synthesizer does not support markup. reducing to plain text");
            }
            final Speak speak = document.getSpeak();
            final String text = speak.getTextContent();
            final SpeakablePlainText speakable = new SpeakablePlainText(text);
            speakPlaintext(speakable);
            return;
        }
        final String doc = document.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speaking SSML");
            LOGGER.debug(doc);
        }
        enableBargeIn = ssmlText.isBargeInEnabled();
        try {
            synthesizer.resume();
            synthesizer.speakMarkup(doc, this);
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
     * @throws NoresourceError
     *         error processing the speakable.
     * @throws BadFetchError
     *         error processing the speakable.
     * @since 0.7.1
     */
    private synchronized void processNextSpeakable()
        throws NoresourceError, BadFetchError {
        // Check if there are more speakables to process
        final SpeakableText speakable;
        synchronized (queuedSpeakables) {
            if (queuedSpeakables.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("no more speakables to process");
                }
                fireQueueEmpty();
                synchronized (emptyLock) {
                    emptyLock.notifyAll();
                }
                return;
            }
            speakable = queuedSpeakables.peek();
        }
        

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing next speakable :" + speakable);
        }

        // Really process the next speakable
        if (speakable instanceof SpeakablePlainText) {
            final SpeakablePlainText text = (SpeakablePlainText) speakable;
            speakPlaintext(text);
        } else if (speakable instanceof SpeakableSsmlText) {
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
     *                the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputStartedEvent(this,
                sessionId, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that the given marker has been reached.
     *
     * @param mark
     *                the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        final SynthesizedOutputEvent event = new MarkerReachedEvent(this,
                sessionId, mark);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output has started.
     *
     * @param speakable
     *                the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputEndedEvent(this,
                sessionId, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event =
            new QueueEmptyEvent(this, sessionId);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output has been updated.
     * @param synthesisResult
     *        the intermediate synthesis result
     */
    private void fireOutputUpdate(final SynthesisResult synthesisResult) {
        final SynthesizedOutputEvent event = new OutputUpdateEvent(this,
                sessionId, synthesisResult);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Speaks a plain text string.
     *
     * @param speakable
     *                speakable containing plain text to be spoken.
     * @exception NoresourceError
     *                    No synthesizer allocated.
     * @exception BadFetchError
     *                    Synthesizer in wrong state.
     */
    private void speakPlaintext(final SpeakablePlainText speakable)
        throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }
        final String text = speakable.getSpeakableText();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("speaking '" + text + "'...");
        }
        try {
            synthesizer.resume();
            synthesizer.speak(text, this);
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
    public void cancelOutput() throws NoresourceError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot queue audio");
        }

        if (!enableBargeIn) {
            return;
        }

        try {
            synthesizer.cancelAll();
        } catch (EngineStateException ee) {
            throw new NoresourceError(ee);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
        if (!enableBargeIn) {
            waitQueueEmpty();
        }
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
     *                Name of the mark.
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
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating output " + queuedSpeakables.size()
                    + "...");
        }

        listeners.clear();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...passivated output");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation info) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation info) {
        sessionId = null;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this resource.
     *
     * @param resourceType
     *                type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUriForNextSynthesisizedOutput()
        throws NoresourceError, URISyntaxException {
        if (synthesizer == null) {
            throw new NoresourceError("No synthesizer!");
        }
        if (locatorFactory == null) {
            return null;
        }
        final AudioManager manager = synthesizer.getAudioManager();
        final String locator = manager.getMediaLocator();
        if (locator == null) {
            return null;
        }
        final URI uri = new URI(locator);
        return locatorFactory.getSinkMediaLocator(this, uri);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        synchronized (queuedSpeakables) {
            return !queuedSpeakables.isEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void speakableUpdate(final SpeakableEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speakable updated: " + event);
        }
        final int id = event.getId();
        SpeakableText speakableText = null;
        if (id == SpeakableEvent.SPEAKABLE_STARTED) {
            hasSentPhones = false;

            synchronized (queuedSpeakables) {
                speakableText = queuedSpeakables.peek();
            }
            fireOutputStarted(speakableText);
        } else if (id == SpeakableEvent.SPEAKABLE_ENDED) {
            synchronized (queuedSpeakables) {
                speakableText = queuedSpeakables.poll();
            }

            fireOutputEnded(speakableText);
            try {
                processNextSpeakable();
            } catch (NoresourceError e) {
                notifyError(e);
            } catch (BadFetchError e) {
                notifyError(e);
            }
        } else if ((id == SpeakableEvent.PHONEME_STARTED) && !hasSentPhones) {
            // Get speakable text that produced this output event
            synchronized (queuedSpeakables) {
                speakableText = queuedSpeakables.peek();
            }

            // Convert phones object types (Jsapi20 -> JVXML)
            final PhoneInfo[] phoneInfos = event.getPhones();
            SpeakablePhoneInfo[] speakablePhones = null;
            if ((phoneInfos != null) && (phoneInfos.length > 0)) {
                speakablePhones = new SpeakablePhoneInfo[phoneInfos.length];
                for (int i = 0; i < phoneInfos.length; i++) {
                    final SpeakablePhoneInfo spi = new SpeakablePhoneInfo(
                            phoneInfos[i].getPhoneme(), phoneInfos[i]
                                    .getDuration());

                    speakablePhones[i] = spi;
                }
            }

            // Make the object that holds the phones
            final SynthesisResult result =
                new Jsapi20SynthesisResult(speakableText, speakablePhones);

            fireOutputUpdate(result);
            hasSentPhones = true;
        }
    }

    /**
     * {@inheritDoc}
     */
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
     * @param error the error event
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
