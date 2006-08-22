/*
 * File:    $RCSfile: AudioOutput.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.beans.PropertyVetoException;
import java.io.OutputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineModeDesc;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import org.jvoicexml.documentserver.DocumentServer;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SpeakablePlainText;
import org.jvoicexml.implementation.SpeakableSsmlText;
import org.jvoicexml.implementation.SpeakableText;
import org.jvoicexml.implementation.SystemOutput;
import org.jvoicexml.implementation.SystemOutputListener;
import org.jvoicexml.implementation.jsapi10.speakstrategy.SpeakStratgeyFactory;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.implementation.jsapi10.jvxml.StreamingAudioPlayer;
import com.sun.speech.freetts.VoiceManager;

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
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class AudioOutput
        implements SystemOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AudioOutput.class);

    /** The used synthesizer. */
    private Synthesizer synthesizer;

    /** The implementation of a TTS engine. */
    private final TTSEngine engine;

    /** The system output listener. */
    private SystemOutputListener listener;

    /**
     * Flag to indicate that TTS output and audio can be cancelled.
     * @todo Replace this by a solution that does not cancel output
     * without bargein, if there is mixed output.
     */
    private boolean enableBargeIn;

    /**
     * Constructs a new audio output.
     *
     * @param platform
     * The platform that manages the TTSEngine.
     */
    public AudioOutput(final Jsapi10Platform platform) {
        engine = platform.getTTSEngine();
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        if (engine == null) {
            throw new NoresourceError("no engine available");
        }

        final EngineModeDesc desc = engine.getEngineProperties();
        try {
            synthesizer = Central.createSynthesizer(desc);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("allocating synthesizer...");
            }

            synthesizer.allocate();
            synthesizer.resume();

            final String voice = engine.getDefaultVoice();
            setVoice(voice);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...synthesizer allocated");
            }
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        } catch (AudioException ae) {
            throw new NoresourceError(ae);
        } catch (PropertyVetoException pve) {
            throw new NoresourceError(pve);
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
    public void setSystemOutputListener(
            final SystemOutputListener outputListener) {
        listener = outputListener;
    }

    /**
     * {@inheritDoc}
     *
     * Checks the type of the given speakable and forwards it either as
     * for SSML output or for plain text output.
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

            queuePlaintext(text);
        } else if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            final SsmlDocument document = ssml.getDocument();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("speaking SSML");
                LOGGER.debug(document.toString());
            }

            final SsmlNode speak = document.getSpeak();
            if (speak == null) {
                return;
            }

            SSMLSpeakStrategy strategy =
                    SpeakStratgeyFactory.getSpeakStrategy(speak);
            if (strategy != null) {
                strategy.speak(this, documentServer, speak);
            }
        } else {
            LOGGER.warn("unsupported speakable: " + speakable);
        }
    }

    /**
     * Speak a plain text string. The text is not interpreted as containing the
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
     * String contains plaing text to be spoken.
     * @exception NoresourceError
     *            No recognizer allocated.
     * @exception BadFetchError
     *            Recognizer in wrong state.
     */
    public void queuePlaintext(final String text)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot speak");
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speaking '" + text + "'...");
        }

        try {
            synthesizer.speakPlainText(text, null);
        } catch (EngineStateError ese) {
            throw new BadFetchError(ese);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void queueAudio(final AudioInputStream audio)
            throws NoresourceError, BadFetchError {
        // This is not necessary, but to be consistent.
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot queue audio");
        }

        if (audio == null) {
            throw new BadFetchError("cannot play a null audio stream");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start playing audio...");
        }

        try {
            /** @todo use the audio stream. */
            final Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            throw new NoresourceError(lue);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done playing audio");
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
     * State to wait for.
     * @exception java.lang.InterruptedException
     * If another thread has interrupted this thread.
     * @throws java.lang.IllegalArgumentException
     * If the specified state is unreachable.
     */
    public void waitEngineState(final long state)
            throws java.lang.InterruptedException,
            java.lang.IllegalArgumentException {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot wait for engine state");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for synthesizer engine state " + state);
        }

        synthesizer.waitEngineState(state);

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
     * @param voiceName
     * Name of the voice to use
     * @throws PropertyVetoException
     * Error in assigning the voice.
     */
    public void setVoice(final String voiceName)
            throws PropertyVetoException {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot set voice");

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting voice to '" + voiceName + "'...");
        }

        final Voice voice = findVoice(voiceName);

        synthesizer.getSynthesizerProperties().setVoice(voice);
    }

    /**
     * Find the voice with the given name.
     *
     * @param voiceName
     * name of the voice to find.
     * @return Voice with the given name, or <code>null</code> if there is no
     * voice with that name.
     */
    private Voice findVoice(final String voiceName) {
        final SynthesizerModeDesc desc =
                (SynthesizerModeDesc) synthesizer
                .getEngineModeDesc();
        final Voice[] voices = desc.getVoices();

        for (int i = 0; i < voices.length; i++) {
            final Voice currentVoice = voices[i];
            final String currentVoiceName = currentVoice.getName();

            if (voiceName.equals(currentVoiceName)) {
                return currentVoice;
            }
        }

        LOGGER.warn("could not find voice '" + voiceName + "'");

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setOutputStream(final OutputStream out)
            throws NoresourceError {
        if (synthesizer == null) {
            throw new NoresourceError(
                    "no synthesizer: cannot set output stream!");
        }

        engine.setOutputStream(synthesizer, out);
    }

    /**
     * A mark in an SSML output has been reached.
     * @param mark Name of the mark.
     */
    public void reachedMark(final String mark) {
        if (listener == null) {
            return;
        }

        listener.markerReached(mark);
    }
}
