/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.speakstrategy;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Utility class to play back an audio file.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
final class AudioFilePlayer implements LineListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(AudioFilePlayer.class);

    /** Reference to the document server to retrieve audio files. */
    private final DocumentServer documentServer;

    /** The current session. */
    private final SessionIdentifier sessionId;

    /** The currently played clip. */
    private Clip clip;

    /** Synchronization of start and end play back. */
    private final Semaphore sem;

    /**
     * Constructs a new object.
     * @param server the document server
     * @param id the id of the current session
     */
    public AudioFilePlayer(final DocumentServer server,
            final SessionIdentifier id) {
        sem = new Semaphore(1);
        documentServer = server;
        sessionId = id;
    }

    /**
     * The audio, delivered by the <code>audio</code> stream is queued after
     * the last element in the speaking queue.
     *
     * <p>
     * If barge-in can be used while queuing the audio depends on the
     * surrounding <code>&lt;prompt&gt;</code>.
     * </p>
     *
     * @param audio
     *        URI of the audio file to play.
     * @exception NoresourceError
     *            The output resource is not available.
     * @exception BadFetchError
     *            Error reading from the <code>AudioStream</code>.
     *
     * @since 0.3
     */
    public void play(final URI audio) throws NoresourceError,
            BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving audio file '" + audio + "'...");
        }
        final AudioInputStream stream = documentServer
                .getAudioInputStream(sessionId, audio);
        if (stream == null) {
            throw new BadFetchError("cannot play a null audio stream");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start playing audio...");
        }

        try {
            sem.acquire();
        } catch (InterruptedException e) {
            LOGGER.info("Waiting to start clip interrupted");
            return;
        }

        try {
            clip = AudioSystem.getClip();
            clip.open(stream);
            clip.addLineListener(this);
            clip.start();
        } catch (javax.sound.sampled.LineUnavailableException e) {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new BadFetchError(ex.getMessage(), ex);
            }
            throw new NoresourceError(e.getMessage(), e);
        } catch (java.io.IOException e) {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new BadFetchError(ex.getMessage(), ex);
            }
            throw new BadFetchError(e.getMessage(), e);
        }

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Waiting for end of clip");
            }
            sem.acquire();
            sem.release();
        } catch (InterruptedException e) {
            throw new BadFetchError(e.getMessage(), e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done playing audio");
        }
    }

    /**
     * Cancels the current output.
     */
    public void cancelOutput() {
        if (clip != null) {
            clip.stop();
            clip = null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final LineEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("line updated: " + event.getType());
        }

        if ((event.getType() == LineEvent.Type.CLOSE)
                || (event.getType() == LineEvent.Type.STOP)) {
            sem.release();
        }
    }
}
