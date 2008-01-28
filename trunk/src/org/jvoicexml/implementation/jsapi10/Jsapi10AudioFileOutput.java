/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;

/**
 * JSAPI 1.0 compliant demo implementation of an {@link AudioFileOutput}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Jsapi10AudioFileOutput implements AudioFileOutput,
        LineListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Jsapi10AudioFileOutput.class);

    /** Reference to the document server to retrieve audio files. */
    private DocumentServer documentServer;

    /** The currently played clip. */
    private Clip clip;

    /** The thread, waiting for the end of the clip. */
    private Thread thread;

    /** Synchronization of start and end play back. */
    private final Semaphore sem;

    /**
     * Constructs a new object.
     */
    public Jsapi10AudioFileOutput() {
        sem = new Semaphore(1);
    }
    /**
     * {@inheritDoc}
     */
    public void queueAudio(final URI audio) throws NoresourceError,
            BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving audio file '" + audio + "'...");
        }

        final AudioInputStream stream = documentServer
                .getAudioInputStream(audio);

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
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            throw new NoresourceError(lue);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Waiting for end of clip");
            }
            sem.acquire();
            sem.release();
        } catch (InterruptedException e) {
            LOGGER.info("Waiting for end of clip interrupted");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done playing audio");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentServer(final DocumentServer server) {
        documentServer = server;
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            LOGGER.info("Waiting to cancel clip interrupted");
            return;
        }

        if (clip != null) {
            clip.stop();
            clip = null;
        }

        if (thread != null) {
            thread.interrupt();
        }

        sem.release();
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

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi10";
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public void update(final LineEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("line updated: " + event.getType());
        }

        if ((event.getType() == LineEvent.Type.CLOSE)
                || (event.getType() == LineEvent.Type.STOP)) {
            sem.release();
        }
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextFileOutput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            LOGGER.info("Waiting to isBusy clip interrupted");
            return false;
        }

        final boolean busy;
        if (clip != null) {
            busy = clip.isActive();
        } else {
            busy = false;
        }

        if (thread != null) {
            thread.interrupt();
        }

        sem.release();

        return busy;
    }
}
