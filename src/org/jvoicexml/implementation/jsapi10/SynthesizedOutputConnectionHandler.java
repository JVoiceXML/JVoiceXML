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

import javax.speech.synthesis.Synthesizer;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.NoresourceError;

/**
 * JSAPI 1.0 does not know how to stream audio from a server to a client.
 * However, custom implementations exist to fill this gap. Classes
 * implementing this interface can be used to address this custom
 * implementation.
 *
 * <p>
 * Note that there is only one object to handle the connections for
 * all synthesizers.
 * </p>
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
public interface SynthesizedOutputConnectionHandler {
    /** Configuration key. */
    String CONFIG_KEY = "connectionhandler";

    /**
     * Establishes a connection from the given {@link RemoteClient} to this
     * object.
     * @param client data container with connection relevant data.
     * @param synthesizer the current synthesizer.
     * @throws IOException
     *         error establishing the connection.
     */
    void connect(final RemoteClient client, final Synthesizer synthesizer)
        throws IOException;

    /**
     * Disconnects a previously established connection.
     * @param client data container with connection relevant data.
     * @param synthesizer the current synthesizer.
     */
    void disconnect(final RemoteClient client, final Synthesizer synthesizer);

    /**
     * Delegate from
     * {@link org.jvoicexml.SynthesizedOuput#getUriForNextSynthesisizedOutput()}
     * .
     * @param client data container with connection relevant data.
     * @return URI of the input source, maybe <code>null</code> if the
     * streaming uses other means of audio output.
     * @throws NoresourceError
     *         Error accessing the device.
     */
    URI getUriForNextSynthesisizedOutput(RemoteClient client)
        throws NoresourceError;
}
