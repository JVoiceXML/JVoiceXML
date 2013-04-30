/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/JVoiceXmlStub.java $
 * Version: $LastChangedRevision: 2430 $
 * Date:    $Date: 2010-12-21 09:21:06 +0100 (Di, 21 Dez 2010) $
 * Author:  $LastChangedBy: schnelle $
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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link Voice}.
 * @author Raphael Groner
 *
 */
public class TestVoice implements TextListener {
    
    private static final long MAX_WAIT = 1000;
    private URI dialog;
    private Voice voice;
    private boolean active;

    /**
     * Set up the test environment
     * @throws IOException
     *         error setting up the test environment
     */
    @Before
    public void setUp() throws IOException {
        dialog = new File("unittests/rc/mock.vxml").toURI();
        voice = new Voice();
        voice.setPolicy("unittests/etc/jvoicexml.policy");
        voice.loadConfiguration("unittests/etc/jndi.properties");
        active = false;
    }

    /**
     * Test case for {@link Voice#lookupJVoiceXML()}.
     * @throws IOException
     *         test failed
     */
    @Test
    public void testLookup() throws IOException {
        // NOTICE: JVoiceXML has to run for test success!!
        Assert.assertNotNull(voice.getJVoiceXml());
        Assert.assertNotNull(voice.getContext());
    }

    /**
     * Test method for {@link Voice#getSession()}.
     * @throws JVoiceXMLEvent
     *         test failed
     * @throws Exception
     *         test failed
     */
    @Test(timeout=10000)
    public void testSession() throws Exception, JVoiceXMLEvent {
        Assert.assertNull(voice.getSession());
        
        final TextServer server = new TextServer(4711);
        server.addTextListener(this);
        server.start();
        synchronized (dialog) {
            dialog.wait(MAX_WAIT);
        }
        try {
            final ConnectionInformation connectionInformation = server.getConnectionInformation();;
            voice.connect(connectionInformation, dialog);
        } finally {
            server.stopServer();
        }
        Assert.assertNull(voice.getSession());
        Assert.assertTrue("active", active);
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
        active = (voice.getSession() != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected() {
    }
}
