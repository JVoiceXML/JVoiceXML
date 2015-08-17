/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.voicexmlunit.demo/src/org/jvoicexml/voicexmlunit/demo/TestHelloDemo.java $
 * Version: $LastChangedRevision: 4259 $
 * Date:    $Date: 2014-09-09 09:09:00 +0200 (Tue, 09 Sep 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.demo;

import java.io.File;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.TextCall;
import org.jvoicexml.voicexmlunit.VoiceXmlUnitNamespaceContect;
import org.jvoicexml.voicexmlunit.XPathAssert;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * A demo that tests proper delivery of audio tags
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 * <p>
 * This demo requires that JVoiceXML is configured with the text
 * implementation platform.
 * </p>
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 *
 */
public final class TestAudioDemo {
    /** The call to JVoiceXML. */
    private Call call;
    /** URI of the application to call. */
    private URI uri;

    /**
     * Set up the test environment.
     * @throws Exception
     *        error setting up the test environment
     */
    @Before
    public void setUp() throws Exception {
        uri = new File("etc/audio.vxml").toURI();
        call = new TextCall();
    }

    /**
     * Tear down the test environment.
     * @exception JVoiceXMLEvent
     *            error tearing down
     */
    @After
    public void tearDown() throws JVoiceXMLEvent {
        call.hangup();
    }

    /**
     * Test the hello world conversation.
     * @exception Exception
     *            test failed
     */
    @Test(timeout = 20000)
    public void testAudioTags() throws Exception {
        call.call(uri);
        final SsmlDocument document1 = call.getNextOutput();
        final URI audio1 = uri.resolve("audio-in-block.wav");
        final VoiceXmlUnitNamespaceContect context =
                new VoiceXmlUnitNamespaceContect();
        context.addPrefix("ssml", Speak.DEFAULT_XMLNS);
        XPathAssert.assertEquals(context, document1,
                "/ssml:speak/ssml:audio/@src", audio1.toString());
        final SsmlDocument document2 = call.getNextOutput();
        final URI audio2 = uri.resolve("audio-in-prompt.wav");
        context.addPrefix("ssml", Speak.DEFAULT_XMLNS);
        XPathAssert.assertEquals(context, document2,
                "/ssml:speak/ssml:audio/@src", audio2.toString());
    }
}
