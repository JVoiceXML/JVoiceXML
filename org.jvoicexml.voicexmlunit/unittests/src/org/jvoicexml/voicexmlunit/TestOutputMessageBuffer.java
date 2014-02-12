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

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link OutputMessageBuffer}.
 * @author Dirk Schnelle-Walka
 *
 */
public class TestOutputMessageBuffer {

    /**
     * Test method for {@link org.jvoicexml.voicexmlunit.OutputMessageBuffer#nextMessage()}.
     * @throws Exception test failed
     * @throws JVoiceXMLEvent test failed
     */
    @Test
    public void testNextMessage() throws Exception, JVoiceXMLEvent {
        final OutputMessageBuffer buffer = new OutputMessageBuffer();
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText("hello world");
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                buffer.outputSsml(document);
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        final SsmlDocument next = buffer.nextMessage();
        Assert.assertEquals(document, next);
    }

    /**
     * Test method for {@link org.jvoicexml.voicexmlunit.OutputMessageBuffer#nextMessage()}.
     * @throws Exception test failed
     * @throws JVoiceXMLEvent test failed
     */
    @Test
    public void testNextMessageMultiple() throws Exception, JVoiceXMLEvent {
        final int max = 1000;
        final OutputMessageBuffer buffer = new OutputMessageBuffer();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<max; i++) {
                    SsmlDocument document = null;
                    try {
                        document = new SsmlDocument();
                    } catch (ParserConfigurationException e) {
                        Assert.fail(e.getMessage());
                    }
                    final Speak speak = document.getSpeak();
                    speak.addText("test " + i);
                    buffer.outputSsml(document);
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        for (int i=0; i<max; i++) {
            final SsmlDocument document = buffer.nextMessage();
            final Speak speak = document.getSpeak();
            Assert.assertEquals("test " + i, speak.getTextContent());
        }
    }
}
