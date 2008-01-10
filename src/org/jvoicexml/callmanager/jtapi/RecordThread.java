/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/callmanager/jtapi/PlayThread.java $
 * Version: $LastChangedRevision: 561 $
 * Date:    $Date: 2007-11-08 11:36:42 +0000 (Qui, 08 Nov 2007) $
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

package org.jvoicexml.callmanager.jtapi;

import java.net.URI;

import javax.telephony.media.MediaResourceException;

import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;

/**
 * Thread to record a stream from a given URI.
 *
 * @author lyncher
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
class RecordThread extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(RecordThread.class);

    /** The URI to record. */
    private final URI uri;

    /** Media service to stream the audio. */
    private final GenericMediaService mediaService;

    /**
     * Constructs a new object.
     * @param service media service to play the audio.
     * @param rtpUri the URI to play.
     */
    public RecordThread(final GenericMediaService service, final URI rtpUri) {
        mediaService = service;
        uri = rtpUri;

        setDaemon(true);
        setName("JTapi RecordThread");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recording uri '" + uri + "'");
        }
        try {
            mediaService.record(uri.toString(), null, null);
        } catch (MediaResourceException ex) {
            LOGGER.error("error recording from URI '" + uri + "'", ex);
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done recording uri '" + uri + "'");
        }
    }
}
