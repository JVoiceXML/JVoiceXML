/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerProperties;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.rtp.RtpConfiguration;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.SynthesizedOutputConnectionHandler;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.jsapi.FreeTTSVoice;

/**
 * An RTP based connection handler for FreeTTS.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
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
            final SynthesizedOutput output, final Synthesizer synthesizer)
            throws IOException {
        final SynthesizerProperties props =
            synthesizer.getSynthesizerProperties();
        final FreeTTSVoice freettsvoice = (FreeTTSVoice) props.getVoice();
        final Voice voice = freettsvoice.getVoice();
        final Jsapi10SynthesizedOutput synthesizedOutput =
            (Jsapi10SynthesizedOutput) output;
        RtpAudioPlayer player = new RtpAudioPlayer(synthesizedOutput);
        voice.setAudioPlayer(player);
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client,
            final SynthesizedOutput output, final Synthesizer synthesizer) {
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput(final RemoteClient client)
        throws NoresourceError {
        final InetAddress address;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new NoresourceError("Error determining local host address",
                    e);
        }

        final RtpConfiguration rtpClient = (RtpConfiguration) client;
        try {
            return new URI("rtp://" + address.getHostName() + ":"
                    + rtpClient.getPort() + "/audio/1");
        } catch (URISyntaxException e) {
            throw new NoresourceError("Error creating URI", e);
        }
    }
}
