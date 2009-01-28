/*
 * File:    $RCSfile: AudioSpeakStrategy.java,v $
 * Version: $Revision: 1.3 $
 * Date:    $Date: 2006/07/17 14:08:55 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;

import javax.sound.sampled.AudioInputStream;

import org.jvoicexml.documentserver.DocumentServer;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.jsapi10.AudioOutput;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.SsmlNode;

/**
 * SSML strategy to play back an <code>&lt;audio&gt;</code> node.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.3 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
class AudioSpeakStrategy
        extends AbstractSpeakStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AudioSpeakStrategy.class);

    /**
     * Constructs a new object.
     */
    public AudioSpeakStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public void speak(final AudioOutput audioOutput,
                      final DocumentServer documentServer, final SsmlNode node)
            throws NoresourceError, BadFetchError {
        final Audio audio = (Audio) node;

        try {
            final AudioInputStream stream =
                    getAudioInputStream(documentServer, audio);
            audioOutput.queueAudio(stream);
        } catch (BadFetchError bfe) {
            LOGGER.info("unable to obtain audio file", bfe);

            speakChildNodes(audioOutput, documentServer, node);
        }
    }

    /**
     * Retrieve an input stream for the referenced audio file.
     * @param documentServer
     *        The document server.
     * @param audio
     *        The audio node to process.
     * @return <code>AudioInputStream</code> for the referenced audio file.
     * @exception BadFetchError
     *            Error retrieving the audio file.
     */
    private AudioInputStream getAudioInputStream(
            final DocumentServer documentServer, final Audio audio)
            throws BadFetchError {
        final String src = audio.getSrc();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving audio file '" + src + "'...");
        }

        final URI uri;
        try {
            uri = new URI(src);
        } catch (java.net.URISyntaxException use) {
            throw new BadFetchError(use);
        }

        return documentServer.getAudioInputStream(uri);
    }
}
