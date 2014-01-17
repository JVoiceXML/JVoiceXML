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

package org.jvoicexml.voicexmlunit.processor;

import org.jvoicexml.voicexmlunit.processor.Voice;
import org.jvoicexml.voicexmlunit.processor.Call;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link Call}.
 * @author Raphael Groner
 *
 */
public class TestCall implements TextListener {
    private Call call;
    private boolean started;
    private boolean connected;
    private boolean activated;

    /**
     * Set up the test environment
     * @throws Exception
     *         setup failed
     */
    @Before
    public void setUp() throws Exception {
        final URI dialog = new File("unittests/etc/mock.vxml").toURI();
        call = new Call(dialog);
        call.setListener(this);

        started = false;
        connected = false;
    }

    @Test
    public void testVoice() {
        // 1. Voice is always valid
        Assert.assertNotNull(call.getVoice());
        // 2. Voice can't be destroyed (self instantiated)
        call.setVoice(null);
        Assert.assertNotNull(call.getVoice());
        // 3. Custom voice
        Voice custom = new Voice();
        call.setVoice(custom);
        Assert.assertSame(custom, call.getVoice());
        // 4. Remove voice
        call.setVoice(null);
        Assert.assertNotSame(custom, call.getVoice());
    }

    @Test
    public void testDialog() throws InterruptedException {
        final Voice voice = call.getVoice();
        Assert.assertNull(voice.getSession());

        activated = false;
        call.run();
        Assert.assertTrue(activated);
        Assert.assertTrue("started", started);
        Assert.assertFalse("connected", connected);
        Assert.assertNull(call.getFailure());
        Assert.assertNull(voice.getSession());
    }

    @Test
    public void testFailure() {
        AssertionError error = new AssertionError();
        call.fail(error);

        Assert.assertNotNull(call.getFailure());
    }

    @Test
    public void testSuccess() {
        call.fail(null);

        Assert.assertNull(call.getFailure());
    }

    @Override
    public void started() {
        started = true;
    }

    @Override
    public void connected(InetSocketAddress remote) {
        connected = true;
        call.startDialog();
    }

    @Override
    public void outputSsml(SsmlDocument document) {
        activated = true;
    }

    @Override
    public void expectingInput() {
    }

    @Override
    public void inputClosed() {

    }

    @Override
    public void disconnected() {
        connected = false;
    }
}
