/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.marc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Demo implementation for a synthesized output for MARC.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public final class MarcSynthesizedOutput implements SynthesizedOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(MarcSynthesizedOutput.class);

    /** Default port of MARC. */
    private static final int MARC_DEFAULT_PORT = 4010;

    /** Type of the created resources. */
    private String type;

    /** Connection to Marc. */
    private DatagramSocket socket;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the created resource.
     * @param typeName name of the resource
     */
    void setType(final String typeName) {
        type = typeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation client) throws IOException {
        socket = new DatagramSocket();
        final InetAddress marc = InetAddress.getLocalHost();
        socket.connect(marc, MARC_DEFAULT_PORT);
        LOGGER.info("connected to MARC at '" + marc + ":" + MARC_DEFAULT_PORT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation client) {
        socket.disconnect();
        socket.close();
        LOGGER.info("diconnected from MARC");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOutput() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SynthesizedOutputListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SynthesizedOutputListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError,
            URISyntaxException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final String sessionId, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        final String utterance;
        if (speakable instanceof SpeakablePlainText) {
            final SpeakablePlainText plain = (SpeakablePlainText) speakable;
            utterance = plain.getSpeakableText();
        } else if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            final SsmlDocument document = ssml.getDocument();
            final Speak speak = document.getSpeak();
            utterance = speak.getTextContent();
        } else {
            utterance = speakable.getSpeakableText();
        }
        try {
            final byte[] buffer = utterance.getBytes();
            final InetAddress marc = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    marc, MARC_DEFAULT_PORT);
            socket.send(packet);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sent utterance '" + utterance + "' to '"
                        + marc + ":" + MARC_DEFAULT_PORT);
            }
            Thread.sleep(1000);
        } catch (SocketException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (UnknownHostException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitQueueEmpty() {
    }
}
