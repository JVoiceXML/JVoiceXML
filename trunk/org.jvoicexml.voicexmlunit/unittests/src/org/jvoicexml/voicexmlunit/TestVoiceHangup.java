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
 * Test cases for {@link VoiceXmlAccessor}.
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public class TestVoiceHangup implements TextListener {

    private URI dialog;
    private VoiceXmlAccessor voice;
    private boolean activated;
    private boolean abortConnection;
    private TextServer server;

    /**
     * Set up the test environment.
     */
    @Before
    public void setUp() {
        dialog = new File("unittests/etc/mock.vxml").toURI();
        voice = new VoiceXmlAccessor();
        server = new TextServer(4711);
        server.addTextListener(this);
    }

    /**
     * Close the test environment.
     */
    @After
    public void tearDown() {
        voice.close();
    }

    /**
     * Test method for {@link VoiceXmlAccessor#getSession()}.
     * Ensures a complete session run without any error.
     * @throws JVoiceXMLEvent
     *         test failed
     * @throws Exception
     *         test failed
     */
    @Test(timeout=9999)
    public void testSessionSuccess() throws Exception, JVoiceXMLEvent {
        abortConnection = false;
        runSession();
    }

    /**
     * Test method for {@link VoiceXmlAccessor#getSession()}.
     * Simulates a session abort.
     * @throws JVoiceXMLEvent
     *         test failed
     * @throws Exception
     *         test failed
     */
    @Test(timeout=9999)
    //@Ignore // test fails due to an imperfection in JVoiceXMLSession
    public void testSessionFailure() throws Exception, JVoiceXMLEvent {
        abortConnection = true;
        runSession();
    }

    private void runSession() throws Exception, JVoiceXMLEvent {
        Assert.assertNull(voice.getSession());

        server.start();
        synchronized (dialog) {
            dialog.wait();
        }
        activated = false;
        try {
            voice.call(server, dialog);
        } finally {
            server.stopServer();
        }

        Assert.assertNull(voice.getSession());
        Assert.assertTrue(activated);
    }

    @Override
    public void started() {
        synchronized (dialog) {
            dialog.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(InetSocketAddress remote) {
        synchronized (dialog) {
            dialog.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputSsml(SsmlDocument document) {
        if (voice.getSession() != null) {
            activated = true;
        }
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
