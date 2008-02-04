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

import javax.telephony.media.MediaResourceException;

import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;
import javax.telephony.media.NotBoundException;
import javax.telephony.media.RTC;
import javax.telephony.media.PlayerConstants;

import java.net.URI;
import java.util.Dictionary;

/**
 * Thread to play a stream from a given URI.
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
class TerminalPlayer extends TerminalMedia {

    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(TerminalPlayer.class);

    /**
     * Constructs a new object.
     * @param service media service to play the audio.
     * @param rtpUri the URI to play.
     */
    public TerminalPlayer(final GenericMediaService service) {
        super(service);
    }

    public void stopProcessing() {
        super.stopProcessing();
        try {
            super.mediaService.triggerRTC(PlayerConstants.rtca_Stop);
        } catch (NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    public void process(URI uri, RTC[] rtc, Dictionary optargs) throws
            MediaResourceException {
        super.mediaService.play(uri.toString(), 0, rtc, optargs);
    }

    public void onPostProcess() {
    }

    public void onPreProcess() {
    }


}
