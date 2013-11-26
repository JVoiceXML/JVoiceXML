/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link Voice}.
 * @author Raphael Groner
 *
 */
public final class TestVoice implements TextListener {

    private URI dialog;
    private Voice voice;
    private TextServer server;
    private boolean abortConnection;
    private SsmlDocument lastOutput;

    /**
     * Set up the test environment.
     */
    @Before
    public void setUp() {
        dialog = new File("unittests/etc/mock.vxml").toURI();
        voice = new Voice();
        server = new TextServer(4711);
        server.addTextListener(this);
        lastOutput = null;
    }

    /**
     * Close the test environment.
     */
    @After
    public void tearDown() {
        voice.close();
    }

    /**
     * Test method for {@link Voice#getSession()}.
     * Ensures a complete session run without any error.
     */
    @Test(timeout=9999)
    public void testSessionSuccess() {
        abortConnection = false;
        runSession();
    }

    /**
     * Test method for {@link Voice#getSession()}.
     * Simulates a session abort.
     */
    @Test(timeout=9999)
    public void testSessionFailure() {
        abortConnection = true;
        runSession();
    }

    private void runSession() {
        Assert.assertNull(voice.getSession());

        server.start();

        try {
            server.waitStarted();
            voice.call(server, dialog);
        } catch (Exception | JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        } finally {
            server.stopServer();
        }

        Assert.assertNull(voice.getSession());
        Assert.assertNotNull(lastOutput);
    }

    @Override
    public void started() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(final InetSocketAddress remote) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputSsml(final SsmlDocument document) {
        lastOutput = document;

        if (abortConnection) {
            server.stopServer(); // enforces session abort without BYE
            voice.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expectingInput() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputClosed() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected() {
    }
}
