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

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Basic wrapper for {@link SystemOutput}.
 *
 * <p>
 * The {@link SystemInput} encapsulates two external resources, the
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
final class JVoiceXmlSystemOutput implements SystemOutput,
        SystemOutputListener {
    /** The synthesizer output device. */
    private final SynthesizedOuput synthesizedOutput;

    /** The audio file output device. */
    private final AudioFileOutput audioFileOutput;

    /**
     * Constructs a new object.
     * @param synthesizer the synthesizer output device.
     * @param file the audio file output device.
     */
    public  JVoiceXmlSystemOutput(final SynthesizedOuput synthesizer,
            final AudioFileOutput file) {
        synthesizedOutput = synthesizer;
        audioFileOutput = file;
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        synthesizedOutput.activate();
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        synthesizedOutput.close();
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return synthesizedOutput.getType();
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        synthesizedOutput.open();
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        synthesizedOutput.passivate();
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        synthesizedOutput.connect(client);
    }

    /**
     * {@inheritDoc}
     */
    public void queueSpeakable(final SpeakableText speakable,
            final boolean bargein, final DocumentServer documentServer)
        throws NoresourceError, BadFetchError {
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
    public void queueAudio(final AudioInputStream audio) throws NoresourceError,
            BadFetchError {
        audioFileOutput.queueAudio(audio);
    }

    /**
     * {@inheritDoc}
     */
    public void markerReached(final String mark) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void outputEnded() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void outputStarted() {
        // TODO Auto-generated method stub
    }
}
