/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.mary;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link MarySynthesizedOutput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class TestMarySynthesizedOutput implements SynthesizedOutputListener {
    /** The test object. */
    private MarySynthesizedOutput output;

    /** Event notification mechanism. */
    private final Object lock = new Object();

    /**
     * Set up the test environment.
     */
    @Before
    public void setUp() {
        output = new MarySynthesizedOutput();
        output.addListener(this);
        output.activate();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mary.MarySynthesizedOutput#queueSpeakable(org.jvoicexml.SpeakableText, org.jvoicexml.DocumentServer)}.
     * @exception Exception test failed
     * @exception JVoiceXMLEvent test failed
     */
    @Test(timeout = 5000)
    public void testQueueSpeakable() throws Exception, JVoiceXMLEvent {
        final SpeakablePlainText plainText =
            new SpeakablePlainText("Hello world");
        output.queueSpeakable(plainText, null);
        synchronized (lock) {
            lock.wait();
        }
        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("hello from SSML");
        final SpeakableSsmlText ssml = new SpeakableSsmlText(doc);
        output.queueSpeakable(ssml, null);
        synchronized (lock) {
            lock.wait();
        }
    }

    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
