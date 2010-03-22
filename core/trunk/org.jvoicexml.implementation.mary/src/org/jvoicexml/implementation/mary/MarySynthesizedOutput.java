/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.mary;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * An implementation of the {@link SynthesizedOutput} for the Mary TTS System.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public class MarySynthesizedOutput implements SynthesizedOutput {

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError,
            URISyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queuePlaintext(String text) throws NoresourceError,
            BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueSpeakable(SpeakableText speakable,
            DocumentServer documentServer) throws NoresourceError,
            BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresAudioFileOutput() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAudioFileOutput(AudioFileOutput fileOutput) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitQueueEmpty() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(RemoteClient client) throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(RemoteClient client) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOutput() throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(SynthesizedOutputListener listener) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(SynthesizedOutputListener listener) {
        // TODO Auto-generated method stub

    }

}
