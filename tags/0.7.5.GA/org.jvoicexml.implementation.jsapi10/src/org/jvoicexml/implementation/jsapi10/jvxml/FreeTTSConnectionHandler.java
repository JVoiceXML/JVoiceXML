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
import java.net.URI;

import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerProperties;

import org.jvoicexml.ConnectionInformation;
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
    public void connect(final ConnectionInformation client,
            final SynthesizedOutput output, final Synthesizer synthesizer)
            throws IOException {
        final SynthesizerProperties props =
            synthesizer.getSynthesizerProperties();
        final FreeTTSVoice freettsvoice = (FreeTTSVoice) props.getVoice();
        final Voice voice = freettsvoice.getVoice();
        final Jsapi10SynthesizedOutput synthesizedOutput =
            (Jsapi10SynthesizedOutput) output;
        StreamableAudioPlayer player =
            new StreamableAudioPlayer(synthesizedOutput);
        voice.setAudioPlayer(player);
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client,
            final SynthesizedOutput output, final Synthesizer synthesizer) {
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput(
            final ConnectionInformation client)
        throws NoresourceError {
        return null;
    }
}
