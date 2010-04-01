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
import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Demo implementation of an {@link AudioFileOutput}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2045 $
 * @since 0.6
 */
public final class MaryAudioFileOutput {
    
	
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(MaryAudioFileOutput.class);

    /** The currently played clip. */
    private Clip clip;

    /** Synchronization of start and end play back. */
    private final Semaphore sem;

    /**
     * Constructs a new object.
     */
    public MaryAudioFileOutput() {
    	
        sem = new Semaphore(1);
    }

    /**
     * {@inheritDoc}
     */
    public void setSynthesizedOutput(final SynthesizedOutput fileOutput) {
    }

    /**
     * {@inheritDoc}
     * @throws BadFetchError 
     * @throws BadFetchError 
     */
    public void queueAudio(ByteArrayInputStream inputStream) throws BadFetchError
           {
  
        LOGGER.info("QUEUE AUDIO");
        final BufferedInputStream buf = new BufferedInputStream(inputStream);
       try{ 
      
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(buf));
 //           clip.addLineListener(this);
            clip.start();
            
        } 
       catch (javax.sound.sampled.LineUnavailableException e) {
            try {
                throw new NoresourceError(e.getMessage(), e);
            } catch (NoresourceError e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Waiting for end of clip");
            }
            sem.acquire();
            sem.release();
        } catch (InterruptedException e) {
            try {
                throw new BadFetchError(e.getMessage(), e);
            } catch (BadFetchError e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done playing audio");
        }
    }

    
    public void cancelOutput() throws NoresourceError {
        if (clip != null) {
            clip.stop();
            clip = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

 
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        clip = null;
    }


    public boolean isBusy() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            LOGGER.warn("Waiting to isBusy clip interrupted");
            return false;
        }

        final boolean busy;
        if (clip != null) {
            busy = clip.isActive();
        } else {
            busy = false;
        }

        sem.release();

        return busy;
    }

}
