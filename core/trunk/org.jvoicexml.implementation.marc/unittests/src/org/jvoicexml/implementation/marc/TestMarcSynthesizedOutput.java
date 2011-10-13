/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.marc;

import java.io.InputStream;

import org.junit.Test;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.xml.sax.InputSource;

/**
 * Test cases for {@link MarcSynthesizedOutput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public class TestMarcSynthesizedOutput {
    /**
     * Test method for {@link org.jvoicexml.implementation.marc.MarcSynthesizedOutput#queueSpeakable(org.jvoicexml.SpeakableText, java.lang.String, org.jvoicexml.DocumentServer)}.
     * @exception Exception test failed
     * @exception JVoiceXMLEvent test failed
     */
    @Test
    public void testQueueSpeakable() throws Exception, JVoiceXMLEvent {
        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("This is a test!");
        final SpeakableSsmlText speakable = new SpeakableSsmlText(doc);
        final MarcSynthesizedOutput output = new MarcSynthesizedOutput();
        output.activate();
        output.connect(null);
        output.queueSpeakable(speakable, null, null);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.marc.MarcSynthesizedOutput#queueSpeakable(org.jvoicexml.SpeakableText, java.lang.String, org.jvoicexml.DocumentServer)}.
     * @exception Exception test failed
     * @exception JVoiceXMLEvent test failed
     */
    @Test
    public void testQueueSpeakableComplex() throws Exception, JVoiceXMLEvent {
        final InputStream in =
                TestMarcSynthesizedOutput.class.getResourceAsStream(
                        "MarcExpressionTest.xml");
        final InputSource source = new InputSource(in);
        final SsmlDocument doc = new SsmlDocument(source);
        final SpeakableSsmlText speakable = new SpeakableSsmlText(doc);
        final MarcSynthesizedOutput output = new MarcSynthesizedOutput();
        output.activate();
        output.connect(null);
        output.queueSpeakable(speakable, null, null);
    }

}
