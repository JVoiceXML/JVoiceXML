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

package org.jvoicexml.implementation.text;

import java.io.IOException;
import java.net.URI;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.text.TextRemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Text based implementation of a {@link AudioFileOutput}.
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
public final class TextAudioFileOutput implements AudioFileOutput {
    /**
     * {@inheritDoc}
     */
    public void setSynthesizedOutput(final SynthesizedOutput fileOutput) {
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextFileOutput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void queueAudio(final URI audio)
        throws NoresourceError, BadFetchError {
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentServer(final DocumentServer server) {
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
        return TextRemoteClient.TYPE;
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

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     *
     * This device is never busy.
     */
    public boolean isBusy() {
        return false;
    }
}
