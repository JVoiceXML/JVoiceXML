/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/AudioFileOutput.java $
 * Version: $LastChangedRevision: 261 $
 * Date:    $Date: 2007-03-28 09:27:22 +0200 (Mi, 28 Mär 2007) $
 * Author:  $LastChangedBy: schnelle $
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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * JSAPI 1.0 compliant demo implemantation of an {@link AudioFileOutput}.
 *
 *
 * @author Dirk Schnelle
 * @version $Revision: 261 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Jsapi10AudioFileOutput implements AudioFileOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(Jsapi10AudioFileOutput.class);

    /** Delay to add to the clip length. */
    private static final int CLIP_DELAY = 300;

    /** Number of milliseconds per second. */
    private static final int MSEC_PER_SEC = 1000;

    /** Reference to the document server to retrieve audio files. */
    private DocumentServer documentServer;

    /**
     * {@inheritDoc}
     */
    public void queueAudio(final URI audio)
        throws NoresourceError, BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving audio file '" + audio + "'...");
        }

        final AudioInputStream stream =
            documentServer.getAudioInputStream(audio);

        if (stream == null) {
            throw new BadFetchError("cannot play a null audio stream");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start playing audio...");
        }

        final Clip clip;
        try {
            clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            throw new NoresourceError(lue);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        long clipLength = (clip.getMicrosecondLength() / MSEC_PER_SEC)
        + CLIP_DELAY;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Wait for playing audio " + clipLength / MSEC_PER_SEC
                    + " sec");
        }

        try {
            Thread.sleep(clipLength);
        } catch (InterruptedException ignore) {
            LOGGER.info("Waiting for end of audio playback interrupted");
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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub
        return null;
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
}
