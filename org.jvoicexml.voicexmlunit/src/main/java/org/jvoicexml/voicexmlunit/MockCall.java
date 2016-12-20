/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.net.URI;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Mocks a real telephone call.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public final class MockCall implements Call {

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(final File file) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(final URI uri) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SsmlDocument getNextOutput() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SsmlDocument getNextOutput(final long timeout) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SsmlDocument getLastOutput() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hears(final String utterance) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hears(final String utterance, final long timeout) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hearsAudio(final URI uri) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hearsAudio(final URI uri, final long timeout) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void say(final String utterance) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void say(final String utterance, final long timeout) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(final String digits) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(final String digits, final long timeout) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitUnitExpectingInput() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitUnitExpectingInput(final long timeout) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hangup() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JVoiceXMLEvent getLastError() {
        return null;
    }
}
