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
import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * Demo implementation for a synthesized output for MARC.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public final class MarcSynthesizedOutput implements SynthesizedOutput {
    /** Type of the created resources. */
    private String type;

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation client) {
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
