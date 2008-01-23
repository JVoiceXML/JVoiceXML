/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.apache.log4j.Logger;
import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesizedOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSystemOutput;
import org.jvoicexml.implementation.SpeakablePlainText;
import org.jvoicexml.implementation.SpeakableSsmlText;
import org.jvoicexml.implementation.SystemOutputListener;
import org.jvoicexml.implementation.jsapi10.speakstrategy.SpeakStratgeyFactory;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Audio output that uses the JSAPI 1.0 to address the TTS engine.
 *
 * <p>
 * Handle all JSAPI calls to the TTS engine to make JSAPI transparent to the
 * interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Jsapi10SynthesizedOutput
        implements SynthesizedOutput, ObservableSystemOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi10SynthesizedOutput.class);

    /** The used synthesizer. */
    private Synthesizer synthesizer;

    /** Reference to the audio file output. */
    private AudioFileOutput audioFileOutput;

    /** The default synthesizer mode descriptor. */
    private final SynthesizerModeDesc desc;

    /** The system output listener. */
    private Collection<SystemOutputListener> listener;

    /** A custom handler to handle remote connections. */
    private SynthesizedOutputConnectionHandler handler;

    /** Type of this resources. */
    private String type;

    /** Reference to a remote client configuration data. */
    private RemoteClient client;

    /**
     * Flag to indicate that TTS output and audio can be canceled.
     *
     * @todo Replace this by a solution that does not cancel output without
     *       bargein, if there is mixed output.
     */
    private boolean enableBargeIn;

    /**
     * Constructs a new audio output.
     *
     * @param defaultDescriptor
     *            the default synthesizer mode descriptor.
     */
    public Jsapi10SynthesizedOutput(
            final SynthesizerModeDesc defaultDescriptor) {
        desc = defaultDescriptor;
        listener = new java.util.ArrayList<SystemOutputListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        try {
            synthesizer = Central.createSynthesizer(desc);
            if (synthesizer == null) {
                throw new NoresourceError("Error creating the synthesizer!");
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("allocating synthesizer...");
            }

            synthesizer.allocate();
            synthesizer.resume();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...synthesizer allocated");
            }
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        } catch (AudioException ae) {
            throw new NoresourceError(ae);
        }
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deallocating synthesizer...");
        }

        try {
            synthesizer.deallocate();
        } catch (EngineException ee) {
            LOGGER.warn("error deallocating synthesizer", ee);
        } finally {
            synthesizer = null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("audio output closed");
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
     *
     * Checks the type of the given speakable and forwards it either as for SSML
     * output or for plain text output.
     */
    public void queueSpeakable(final SpeakableText speakable,
                               final boolean bargein,
                               final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        enableBargeIn = bargein;

        if (speakable instanceof SpeakablePlainText) {
            final String text = speakable.getSpeakableText();

            queuePlaintextMessage(text);
        } else if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;

            queueSpeakableMessage(ssml, documentServer);
        } else {
            LOGGER.warn("unsupported speakable: " + speakable);
        }
    }

    /**
     * Queues the speakable SSML formatted text.
     *
     * @param text
     *            SSML formatted text.
     * @param documentServer
     *            The DocumentServer to use.
     * @exception NoresourceError
     *                The output resource is not available.
     * @exception BadFetchError
     *                Error reading from the <code>AudioStream</code>.
     */
    private void queueSpeakableMessage(final SpeakableSsmlText text,
                                       final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        fireOutputStarted();

        final SsmlDocument document = text.getDocument();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speaking SSML");
            LOGGER.debug(document.toString());
        }

        final SsmlNode speak = document.getSpeak();
        if (speak == null) {
            return;
        }

        final SSMLSpeakStrategy strategy =
                SpeakStratgeyFactory.getSpeakStrategy(speak);
        if (strategy != null) {
            strategy.speak(this, audioFileOutput, speak);
        }
    }

    /**
     * Notifies all listeners that output has started.
     */
    private void fireOutputStarted() {
        synchronized (listener) {
            for (SystemOutputListener current : listener) {
                current.outputStarted();
            }
        }
    }

    /**
     * Notifies all listeners that the given marker has been reached.
     * @param mark the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        synchronized (listener) {
            for (SystemOutputListener current : listener) {
                current.markerReached(mark);
            }
        }
    }

    /**
     * Speaks a plain text string. The text is not interpreted as containing the
     * Java Speech Markup Language so JSML elements are ignored. The text is
     * placed at the end of the speaking queue and will be spoken once it
     * reaches the top of the queue and the synthesizer is in the RESUMED state.
     * In other respects it is similar to the speak method that accepts a
     * Speakable object.
     * <p>
     * The source of a SpeakableEvent issued to the SpeakableListener is the
     * String object.
     * </p>
     * <p>
     * The speak methods operate as defined only when a Synthesizer is in the
     * ALLOCATED state. The call blocks if the Synthesizer in the
     * ALLOCATING_RESOURCES state and completes when the engine reaches the
     * ALLOCATED state. An error is thrown for synthesizers in the DEALLOCATED
     * or DEALLOCATING_RESOURCES states.
     * </p>
     *
     * @param text
     *            String contains plain text to be spoken.
     * @exception NoresourceError
     *                No recognizer allocated.
     * @exception BadFetchError
     *                Recognizer in wrong state.
     *
     * @since 0.6
     */
    public void queuePlaintextMessage(final String text)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot speak");
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        fireOutputStarted();

        queuePlaintext(text);
    }

    /**
     * Speaks a plain text string.
     *
     * @param text
     *            String contains plain text to be spoken.
     * @exception NoresourceError
     *                No recognizer allocated.
     * @exception BadFetchError
     *                Recognizer in wrong state.
     */
    public void queuePlaintext(final String text)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot speak");
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        LOGGER.info("speaking '" + text + "'...");

        try {
            synthesizer.speakPlainText(text, null);
        } catch (EngineStateError ese) {
            throw new BadFetchError(ese);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput()
            throws NoresourceError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot queue audio");
        }

        if (!enableBargeIn) {
            return;
        }

        try {
            synthesizer.cancelAll();
        } catch (EngineStateError ee) {
            throw new NoresourceError(ee);
        }
    }

    /**
     * Blocks the calling thread until the Engine is in a specified state.
     * <p>
     * All state bits specified in the state parameter must be set in order for
     * the method to return, as defined for the testEngineState method. If the
     * state parameter defines an unreachable state (e.g. PAUSED | RESUMED) an
     * exception is thrown.
     * </p>
     * <p>
     * The waitEngineState method can be called successfully in any Engine
     * state.
     * </p>
     *
     * @param state
     *            State to wait for.
     * @exception java.lang.InterruptedException
     *                If another thread has interrupted this thread.
     */
    public void waitEngineState(final long state)
            throws java.lang.InterruptedException {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot wait for engine state");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for synthesizer engine state " + state);
        }

        final long current = synthesizer.getEngineState();
        if (current != state) {
            synthesizer.waitEngineState(state);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reached engine state " + state);
        }
    }

    /**
     * Convenient method to wait until all output is being played.
     */
    public void waitQueueEmpty() {
        try {
            waitEngineState(Synthesizer.QUEUE_EMPTY);
        } catch (InterruptedException ie) {
            LOGGER.error("error waiting for empty queue", ie);
        }
    }

    /**
     * A mark in an SSML output has been reached.
     *
     * @param mark
     *            Name of the mark.
     */
    public void reachedMark(final String mark) {
        if (listener == null) {
            return;
        }

        fireMarkerReached(mark);
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating output...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating output...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient remoteClient)
        throws IOException {
        if (handler != null) {
            handler.connect(remoteClient, synthesizer);
        }

        client = remoteClient;
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient remoteClient) {
        if (handler != null) {
            handler.disconnect(remoteClient, synthesizer);
        }

        client = null;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this resource.
     * @param resourceType type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * {@inheritDoc}
     */
    public void setAudioFileOutput(final AudioFileOutput fileOutput) {
        audioFileOutput = fileOutput;
    }

    /**
     * Sets a custom connection handler.
     * @param connectionHandler the connection handler.
     */
    public void setSynthesizedOutputConnectionHandler(
            final SynthesizedOutputConnectionHandler connectionHandler) {
        handler = connectionHandler;
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        if (handler != null) {
            return handler.getUriForNextSynthesisizedOutput(client);
        }

        return null;
    }
}
