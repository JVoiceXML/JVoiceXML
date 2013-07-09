/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/JVoiceXmlMain.java $
 * Version: $LastChangedRevision: 3147 $
 * Date:    $Date: 2012-05-24 09:59:19 +0200 (Thu, 24 May 2012) $
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

package org.jvoicexml.voicexmlunit.demo.input;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.voicexmlunit.Voice;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Input demo for JVoiceXMLUnit.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>etc</code> folder added to the classpath.
 * </p>
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3745 $
 * @since 0.7.6
 */
public final class InputDemo implements TextListener {

    private static final int PORT = 4243;

    private static final long MAX_WAIT = 1000;

    private Voice voice;

    private TextServer server;

    private String result;

    private URI dialog;

    private String input;

    @Before
    public void setUp() {
        voice = new Voice();
        dialog = new File("etc/input.vxml").toURI();

        server = new TextServer(PORT);
        server.addTextListener(this);
        server.start();
    }

    @After
    public void tearDown() {
        server.stopServer();
    }

    @Test(timeout = 10000)
    public void testInputYes() throws IOException {
        testInput("yes");
    }

    @Test(timeout = 10000)
    public void testInputNo() throws IOException {
        boolean failed = false;
        try {
            testInput("no");
        } catch (AssertionFailedError e) {
            failed = true;
        } finally {
            Assert.assertTrue(failed);
        }
    }

    private void testInput(final String answer) throws IOException {
        try {
            synchronized (server) {
                server.wait(MAX_WAIT);
            }
            input = answer;
            interpretDocument(dialog);
            synchronized (server) {
                server.wait(MAX_WAIT);
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        final String expected = "You like this example.";
        Assert.assertEquals(expected, result);
    }

    private void interpretDocument(final URI uri) throws IOException {
        voice.call(server, uri);
    }

    @Override
    public void started() {
        synchronized (server) {
            server.notifyAll();
        }
    }

    @Override
    public void connected(InetSocketAddress remote) {
        synchronized (server) {
            server.notifyAll();
        }
    }

    @Override
    public void outputSsml(SsmlDocument document) {
        result = document.getSpeak().getTextContent();
    }

    @Override
    public void expectingInput() {
        try {
            server.sendInput(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inputClosed() {

    }

    @Override
    public void disconnected() {

    }
}
