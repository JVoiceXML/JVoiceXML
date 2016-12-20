/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextMessageEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link TextCall}.
 * 
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public final class TestCall implements TextListener {
    private TextCall call;
    private boolean started;
    private boolean connected;
    private SsmlDocument lastOutput;
    /** URI of the application to call. */
    private URI uri;

    /**
     * Set up the test environment.
     * 
     * @throws Exception
     *             setup failed
     */
    @Before
    public void setUp() throws Exception {
        uri = new File("unittests/etc/mock.vxml").toURI();
        call = new TextCall();
        call.addTextListener(this);

        started = false;
        connected = false;
    }

    @Test(timeout = 5000)
    public void testDialog() throws InterruptedException {
        lastOutput = null;
        call.call(uri);
        Assert.assertEquals(lastOutput, call.getNextOutput());
        Assert.assertTrue("started", started);
        Assert.assertFalse("connected", connected);
    }

    @Override
    public void started() {
        started = true;
    }

    @Override
    public void connected(final InetSocketAddress remote) {
        connected = true;
    }

    @Override
    public void outputSsml(final TextMessageEvent event,
            final SsmlDocument document) {
        lastOutput = document;
    }

    @Override
    public void expectingInput(final TextMessageEvent event) {
    }

    @Override
    public void inputClosed(final TextMessageEvent event) {

    }

    @Override
    public void disconnected(final TextMessageEvent event) {
        connected = false;
    }
}
