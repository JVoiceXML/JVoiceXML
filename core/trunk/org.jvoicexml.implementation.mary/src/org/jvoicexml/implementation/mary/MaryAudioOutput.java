/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $LastChangedDate $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.mary;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

/**
 * Lineoutput for Mary.
 *
 * @author Dirk Schnelle-Walka
 * @author Giannis Assiouras
 * @version $Revision$
 * @since 0.6
 */
public final class MaryAudioOutput implements LineListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MaryAudioOutput.class);

    /** The currently played clip. */
    private Clip clip;

    /**Flag that indicates if there is currently an audio playing. */
    private boolean isBusy;
    
    
    /**Object in which SynthesisQueue Thread waits until.
     * audio playing is complete */
    private final Object audioPlayedLock;

    /**
     * Constructs a new object.
     */
    public MaryAudioOutput() {
        audioPlayedLock = new Object();
    }

    /**
     * Plays back the audio that can be retrieved from the given
     * input stream.
     * @throws LineUnavailableException if a clip object is not available
     *         due to resource restrictions
     * @throws IOException if an I/O Exception occurs
     * @throws UnsupportedAudioFileException if the stream does not point to
     *         valid audio file data recognized by the system
     * @param  inputStream that contains the processed data from the Mary Server
     */
    public void playAudio(final InputStream inputStream)
        throws LineUnavailableException, IOException,
            UnsupportedAudioFileException {
        isBusy = true;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("playing audio");
        }

        final BufferedInputStream buf = new BufferedInputStream(inputStream);
        clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(buf));
        clip.addLineListener(this);
        clip.start();
    }

    /**
     * Cancels the currently playing audio.
     */
    public void cancelOutput() {
        if (clip != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("cancel output requested");
            }
            clip.stop();
            clip = null;
            isBusy = false;
            synchronized (audioPlayedLock) {
                audioPlayedLock.notify();
            }
        }
    }

    /**
     * Checks if there is currently an audio playing.
     * @return <code>true</code> if there is an active output.
     */
    public boolean isBusy() {
        return isBusy;
    }

    /**
     * Waits until the previous audio playing has completed.
     */
    public void waitAudioPlaying() {
        synchronized (audioPlayedLock) {
            if (isBusy()) {
                try {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("waiting for end of audio");
                    }
                    audioPlayedLock.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Notifies the SynthesisQueue Thread that the audio has been played.
     */
    @Override
    public void update(final LineEvent event) {
        if ((event.getType() == LineEvent.Type.CLOSE)
                || (event.getType() == LineEvent.Type.STOP)) {
            clip.close();
            clip = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("audio playing ended");
            }
            isBusy = false;
            synchronized (audioPlayedLock) {
                audioPlayedLock.notify();
            }
        }
     }    
}
