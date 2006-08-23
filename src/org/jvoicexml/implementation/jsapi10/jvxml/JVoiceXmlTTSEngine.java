/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.OutputStream;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineModeDesc;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;
import org.apache.log4j.Logger;
import org.jvoicexml.implementation.jsapi10.TTSEngine;

/**
 * Audio output that uses <a href="http://freetts.sourceforge.net">FreeTTS</a>
 * as the TTS engine.
 *
 * <p>
 * Support for audio output using audio files and text-to-speech (TTS). The
 * implementation platform must be able to freely sequence TTS and audio output.
 * </p>
 *
 * <p>
 * <b>NOTE:</b> this uses the Central class of JSAPI to find a Synthesizer. The
 * Central class expects to find a speech.properties file in <em>user.home</em>
 * or <em>java.home/lib</em>.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class JVoiceXmlTTSEngine
        implements TTSEngine {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlTTSEngine.class);

    /**
     * Create a new object.
     */
    public JVoiceXmlTTSEngine() {
    }

    /**
     * Register the speech engines with the Central class for use by JVoiceXML.
     *
     * @see javax.speech.EngineCentral
     */
    public void registerEngines() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("registering FreeTTS engine central...");
            }
            Central.registerEngineCentral(FreeTTSEngineCentral.class.getName());
        } catch (EngineException ee) {
            LOGGER.error("error registering engine central", ee);
        }
    }

    /**
     * Get the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     * engine selection
     */
    public EngineModeDesc getEngineProperties() {
        return new SynthesizerModeDesc(null, null, Locale.US, null, null);
    }

    /**
     * Get the name of the default Voice.
     *
     * @return Name of the default voice.
     */
    public String getDefaultVoice() {
        return "kevin16";
    }

    /**
     * {@inheritDoc}
     */
    public void setOutputStream(final Synthesizer synthesizer,
                                final OutputStream out) {
        // Do nothing, if no OutputStream is given.
        if (out == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "no output stream given. Using default output for TTS");
            }

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting output stream to '" + out + "'...");
        }

        // Create a new AudioPlayer and set it as output.
        final SynthesizerModeDesc desc =
                (SynthesizerModeDesc) synthesizer.getEngineModeDesc();

        final Voice[] voices = desc.getVoices();

        final StreamingAudioPlayer player = new StreamingAudioPlayer(out);

        for (int i = 0; i < voices.length; i++) {
            final Voice voice = voices[i];
            if (voice instanceof com.sun.speech.freetts.jsapi.FreeTTSVoice) {
                com.sun.speech.freetts.jsapi.FreeTTSVoice freettsvoice =
                        (com.sun.speech.freetts.jsapi.FreeTTSVoice) voice;
                com.sun.speech.freetts.Voice vc = freettsvoice.getVoice();
                vc.setAudioPlayer(player);
            }
        }

    }
}
