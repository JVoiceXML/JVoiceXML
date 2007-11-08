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

package org.jvoicexml.callmanager.jtapi;

import java.net.URI;

import javax.telephony.media.MediaResourceException;

import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;

/**
 * Thread to play a stream from a given URI.
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
class PlayThread extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(PlayThread.class);

    /** The URI to play. */
    private final URI uri;

    /** Media service to stream the audio. */
    private final GenericMediaService mediaService;

    /**
     * Constructs a new object.
     * @param service media service to play the audio.
     * @param rtpUri the URI to play.
     */
    public PlayThread(final GenericMediaService service, final URI rtpUri) {
        mediaService = service;
        uri = rtpUri;

        setDaemon(true);
        setName("JTapi PlayThread");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("playing uri '" + uri + "'");
        }
        try {
            mediaService.play(uri.toString(), 0, null, null);
        } catch (MediaResourceException ex) {
            LOGGER.error("error playing from URI '" + uri + "'", ex);
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done playing uri '" + uri + "'");
        }
    }
}
