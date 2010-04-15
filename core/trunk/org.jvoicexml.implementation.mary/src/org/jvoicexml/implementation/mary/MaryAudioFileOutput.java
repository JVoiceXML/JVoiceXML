/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/Jsapi10AudioFileOutput.java $
 * Version: $LastChangedRevision: 2045 $
 * Date:    $Date: 2010-02-22 17:49:11 +0100 (Mo, 22 Feb 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;

/**
 * Demo implementation of an {@link AudioFileOutput}.
 *
 * @author Dirk Schnelle-Walka
 * @author Giannis Assiouras
 * @version $Revision: 2045 $
 * @since 0.6
 */
public final class MaryAudioFileOutput implements LineListener {


    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(MaryAudioFileOutput.class);

    /** The currently played clip. */
    private Clip clip;


    /**Object in which SynthesisQueue Thread waits until.
     * audio playing is complete */
    private final Object audioPlayedLock;

    /**
     * Constructs a new object.
     * @param lock object in which SynthesisQueue Thread waits until
     * audio playing is complete
     */
    public MaryAudioFileOutput(final Object lock) {

        audioPlayedLock = lock;

    }

  /**
     * @throws LineUnavailableException if a clip object is not available
     *         due to resource restrictions
     * @throws IOException if an I/O Exception occurs
     * @throws UnsupportedAudioFileException if the stream does not point to
     *         valid audio file data recognized by the system
     * @param  inputStream that contains the processed data from the Mary Server

     */
    public void queueAudio(final ByteArrayInputStream inputStream)
        throws LineUnavailableException, IOException,
            UnsupportedAudioFileException {

        LOGGER.info("Queuing Audio");
        final BufferedInputStream buf = new BufferedInputStream(inputStream);



            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(buf));
            clip.addLineListener(this);
            clip.start();
            
            inputStream.close();

    }


    /**Cancels the currently playing audio.
     * @throws NoresourceError .
     * */

    public void cancelOutput() throws NoresourceError {
        LOGGER.info("cancel output requested"+clip);
        if (clip!= null) {
            LOGGER.info("cancel output requested");
            clip.stop();
            clip = null;
        }
    }


    /**Checks if there is currently an audio playing.
     * @return boolean
     * */

    public boolean isBusy() {

        final boolean busy;
        if (clip != null) {
            busy = clip.isActive();
        } else {
            busy = false;
        }
        System.out.println(busy);
        return busy;
    }

    @Override
    /*Notifies the SynthesisQueue Thread that the audio has been played*/
     public void update(final LineEvent event) {
        if  ((event.getType() == LineEvent.Type.CLOSE)
                || (event.getType() == LineEvent.Type.STOP)) {
            SynthesisQueue.audioPlayed = true;
            synchronized (audioPlayedLock) {
                  audioPlayedLock.notify();
            }
        }
    }

}
