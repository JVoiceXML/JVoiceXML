/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;

import javax.media.rtp.SessionManagerException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerProperties;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.rtp.RtpRemoteClient;
import org.jvoicexml.implementation.jsapi10.SynthesizedOutputConnectionHandler;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.jsapi.FreeTTSVoice;

/**
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class FreeTTSConnectionHandler
    implements SynthesizedOutputConnectionHandler {

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client,
            final Synthesizer synthesizer)
            throws IOException {
        SynthesizerProperties props = synthesizer.getSynthesizerProperties();
        FreeTTSVoice freettsvoice = (FreeTTSVoice) props.getVoice();
        Voice voice = freettsvoice.getVoice();
        RtpServer server = RtpServer.getInstance();
        RtpRemoteClient rtpClient = (RtpRemoteClient) client;
        try {
            server.addTarget(rtpClient.getAddress(), rtpClient.getPort());
        } catch (SessionManagerException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }

        RtpAudioPlayer player = new RtpAudioPlayer();
        voice.setAudioPlayer(player);
    }
}
