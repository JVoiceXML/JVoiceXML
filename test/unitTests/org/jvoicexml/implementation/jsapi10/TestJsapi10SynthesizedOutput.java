/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/test/unitTests/org/jvoicexml/implementation/jsapi10/TestSynthesizerModeDescFactory.java $
 * Version: $LastChangedRevision: 541 $
 * Date:    $Date: 2007-11-02 09:26:54 +0100 (Fr, 02 Nov 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.SynthesizerModeDesc;

import junit.framework.TestCase;

import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

/**
 * Test cases for {@link JVoiceXmlSynthesizerModeDescFactory}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 541 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestJsapi10SynthesizedOutput extends TestCase {
    /** The test object. */
    private Jsapi10SynthesizedOutput synthesizer;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        Central.registerEngineCentral(FreeTTSEngineCentral.class.getName());
        final SynthesizerModeDesc desc = new SynthesizerModeDesc(Locale.US);
        synthesizer = new Jsapi10SynthesizedOutput(desc);
        try {
            synthesizer.open();
        } catch (NoresourceError e) {
            throw new Exception(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        synthesizer.close();
        super.tearDown();
    }

    /**
     * {@inheritDoc}
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    public void testQueueSpeakable() throws JVoiceXMLEvent {
        final SpeakableText speakable1 =
            new SpeakablePlainText("this is a test");
        synthesizer.queueSpeakable(speakable1, false, null);
        assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        assertFalse(synthesizer.isBusy());

        final SpeakableText speakable2 =
            new SpeakablePlainText("this is another test");
        synthesizer.queueSpeakable(speakable1, false, null);
        synthesizer.queueSpeakable(speakable2, false, null);
        assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        assertFalse(synthesizer.isBusy());
    }
}
