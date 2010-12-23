/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.net.URI;

import javax.speech.recognition.Recognizer;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.jsapi10.Jsapi10SpokenInput;
import org.jvoicexml.implementation.jsapi10.SpokenInputConnectionHandler;

/**
 * RTP based connection handler for sphinx 4.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Sphinx4ConnectionHandler
    implements SpokenInputConnectionHandler {

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation client,
            final SpokenInput spokenInput, final Recognizer recognizer)
            throws IOException {
        final Sphinx4Recognizer sphinx = (Sphinx4Recognizer) recognizer;
        final StreamableMicrophone microphone =
            (StreamableMicrophone) sphinx.getDataProcessor();
        final Jsapi10SpokenInput input = (Jsapi10SpokenInput) spokenInput;
        input.setStreamableSpokenInput(microphone);

    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client, final SpokenInput input,
            final Recognizer recognizer) {
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput(final ConnectionInformation client)
            throws NoresourceError {
        // TODO Auto-generated method stub
        return null;
    }

}

