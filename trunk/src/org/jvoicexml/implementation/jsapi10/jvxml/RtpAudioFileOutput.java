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
package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Dummy implementation of an RTP audio file output.
 * TODO Implement this class.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class RtpAudioFileOutput
        implements AudioFileOutput {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(RtpAudioFileOutput.class);

    /**
     * {@inheritDoc}
     */
    public void queueAudio(final URI audio)
        throws NoresourceError, BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentServer(final DocumentServer server) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextFileOutput() throws NoresourceError {
        // TODO Auto-generated method stub
        return null;
    }

}
