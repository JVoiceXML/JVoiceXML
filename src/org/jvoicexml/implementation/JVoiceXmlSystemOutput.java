/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Basic wrapper for {@link SystemOutput}.
 *
 * <p>
 * The {@link JVoiceXmlSystemOutput} encapsulates two external resources, the
 * {@link SynthesizedOuput} and the {@link AudioFileOutput}. Both resources
 * are obtained from a pool using the same type.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class JVoiceXmlSystemOutput
    implements SystemOutput, ObservableSynthesizedOutput, AudioFileOutputProvider,
    SynthesizedOutputProvider {
    /** The synthesizer output device. */
    private final SynthesizedOutput synthesizedOutput;

    /** The audio file output device. */
    private final AudioFileOutput audioFileOutput;

    /**
     * Constructs a new object.
     * @param synthesizer the synthesizer output device.
     * @param file the audio file output device.
     */
    public JVoiceXmlSystemOutput(final SynthesizedOutput synthesizer,
            final AudioFileOutput file) {
        synthesizedOutput = synthesizer;
        audioFileOutput = file;

        synthesizedOutput.setAudioFileOutput(audioFileOutput);
        audioFileOutput.setSynthesizedOutput(synthesizedOutput);
    }

    /**
     * Retrieves the synthesized output resource.
     * @return the synthesized output resource.
     */
    public SynthesizedOutput getSynthesizedOutput() {
        return synthesizedOutput;
    }

    /**
     * Retrieves the audio file output resource.
     * @return the audio file output resource.
     */
    public AudioFileOutput getAudioFileOutput() {
        return audioFileOutput;
    }

    /**
     * {@inheritDoc}
     */
    public void queueSpeakable(final SpeakableText speakable,
            final boolean bargein, final DocumentServer documentServer)
        throws NoresourceError, BadFetchError {
        audioFileOutput.setDocumentServer(documentServer);
        synthesizedOutput.queueSpeakable(speakable, bargein, documentServer);
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
        synthesizedOutput.cancelOutput();
        audioFileOutput.cancelOutput();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SynthesizedOutputListener listener) {
        if (synthesizedOutput instanceof ObservableSynthesizedOutput) {
            final ObservableSynthesizedOutput observable =
                (ObservableSynthesizedOutput) synthesizedOutput;
            observable.addListener(listener);
        }

        if (audioFileOutput instanceof ObservableSynthesizedOutput) {
            final ObservableSynthesizedOutput observable =
                (ObservableSynthesizedOutput) audioFileOutput;
            observable.addListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(
            final SynthesizedOutputListener listener) {
        if (synthesizedOutput instanceof ObservableSynthesizedOutput) {
            final ObservableSynthesizedOutput observable =
                (ObservableSynthesizedOutput) synthesizedOutput;
            observable.removeListener(listener);
        }

        if (audioFileOutput instanceof ObservableSynthesizedOutput) {
            final ObservableSynthesizedOutput observable =
                (ObservableSynthesizedOutput) audioFileOutput;
            observable.removeListener(listener);
        }
    }

    /**
     * Checks if the corresponding output device is busy.
     * @return <code>true</code> if the output devices is busy.
     */
    public boolean isBusy() {
        return synthesizedOutput.isBusy();
    }
}
