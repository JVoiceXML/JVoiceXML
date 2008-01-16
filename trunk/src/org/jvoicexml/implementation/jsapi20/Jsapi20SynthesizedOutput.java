/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/Jsapi10SynthesizedOutput.java $
 * Version: $LastChangedRevision: 435 $
 * Date:    $Date: 2007-09-07 08:49:43 +0100 (Sex, 07 Set 2007) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.implementation.jsapi20;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URI;

import javax.speech.AudioException;
import javax.speech.EngineManager;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

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
import org.jvoicexml.implementation.jsapi20.speakstrategy.SpeakStratgeyFactory;
import org.apache.log4j.Logger;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;
import java.net.URISyntaxException;

/**
 * Audio output that uses the JSAPI 2.0 to address the TTS engine.
 *
 * <p>
 * Handle all JSAPI calls to the TTS engine to make JSAPI transparent to the
 * interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 435 $
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Jsapi20SynthesizedOutput
        implements SynthesizedOutput, ObservableSystemOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi20SynthesizedOutput.class);

    /** The used synthesizer. */
    private Synthesizer synthesizer;

    /** Reference to the audio file output. */
    private AudioFileOutput audioFileOutput;

    /** The default synthesizer mode descriptor. */
    private final SynthesizerMode desc;

    /** The system output listener. */
    private SystemOutputListener listener;

    /** Name of the voice to use. */
    private String voiceName;

    /** Type of this resources. */
    private String type;

    /** Reference to a remote client configuration data. */
    private RemoteClient client;

    private String mediaLocator;

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
    public Jsapi20SynthesizedOutput(
            final SynthesizerMode defaultDescriptor, final String mediaLocator) {
        desc = defaultDescriptor;
        this.mediaLocator = mediaLocator;
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        try {
            synthesizer = (Synthesizer) EngineManager.createEngine(desc);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("allocating synthesizer...");
            }

            try {
                synthesizer.getAudioManager().setMediaLocator(mediaLocator);
                synthesizer.allocate();
            } catch (EngineStateException ex) {
                ex.printStackTrace();
            } catch (EngineException ex) {
                ex.printStackTrace();
            } catch (AudioException ex) {
                ex.printStackTrace();
            }

            if (voiceName != null) {
                setVoice(voiceName);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...synthesizer allocated");
            }
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        } catch (PropertyVetoException e) {
            throw new NoresourceError(e);
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
        } catch (AudioException ex) {
        } catch (EngineStateException ee) {
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
    public void setSystemOutputListener(
            final SystemOutputListener outputListener) {
        listener = outputListener;
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
        if (listener != null) {
            listener.outputStarted();
        }

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

        if (listener != null) {
            listener.outputStarted();
        }

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

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("speaking '" + text + "'...");
        }

        try {
            synthesizer.speak(text, null);
        } catch (EngineStateException ese) {
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
        } catch (EngineStateException ee) {
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
     * Use the given voice for the synthesizer.
     *
     * @param name
     *            Name of the voice to use
     * @throws PropertyVetoException
     *             Error in assigning the voice.
     */
    public void setVoice(final String name)
            throws PropertyVetoException {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot set voice");

            voiceName = name;

            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("setting voice to '" + voiceName + "'...");
        }

        final Voice voice = findVoice(voiceName);

        synthesizer.getSynthesizerProperties().setVoice(voice);
    }

    /**
     * Find the voice with the given name.
     *
     * @param name
     *            name of the voice to find.
     * @return Voice with the given name, or <code>null</code> if there is no
     *         voice with that name.
     */
    private Voice findVoice(final String name) {
        final SynthesizerMode currentDesc =
                (SynthesizerMode) synthesizer
                .getEngineMode();
        final Voice[] voices = currentDesc.getVoices();

        for (int i = 0; i < voices.length; i++) {
            final Voice currentVoice = voices[i];
            final String currentVoiceName = currentVoice.getName();

            if (name.equals(currentVoiceName)) {
                return currentVoice;
            }
        }

        LOGGER.warn("could not find voice '" + name + "'");

        return null;
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

        listener.markerReached(mark);
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


        client = remoteClient;
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient remoteClient) {


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
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        if (synthesizer != null) {
            try {
                URI uri = new URI(synthesizer.getAudioManager().getMediaLocator());
                if (uri.getQuery() != null) {
                    String[] parametersString = uri.getQuery().split("\\&");
                    for (String part : parametersString) {
                        String[] queryElement = part.split("\\=");
                        if (queryElement[0].equals("participant")) {
                            String participantUri = uri.getScheme();
                            participantUri += "://";
                            participantUri += queryElement[1];
                            participantUri += "/audio";
                            return new URI(participantUri);
                        }
                    }
                }
                return uri;
            } catch (URISyntaxException ex) {
                return null;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void waitOutputEnd() throws NoresourceError {
        waitQueueEmpty();
    }

}
