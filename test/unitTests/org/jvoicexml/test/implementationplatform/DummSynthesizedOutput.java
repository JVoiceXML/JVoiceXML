/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.test.implementationplatform;

import java.io.IOException;
import java.net.URI;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * This class provides a dummy {@link SynthesizedOutput} for testing
 * purposes.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class DummSynthesizedOutput implements SynthesizedOutput {
    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void queuePlaintext(final String text) throws NoresourceError,
            BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void queueSpeakable(final SpeakableText speakable,
            final boolean bargein, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void setAudioFileOutput(final AudioFileOutput fileOutput) {
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
        return "dummy";
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
    public boolean isBusy() {
        // TODO Auto-generated method stub
        return false;
    }

}
