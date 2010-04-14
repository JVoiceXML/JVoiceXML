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

import java.io.IOException;
import java.util.Locale;

import marytts.client.MaryClient;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.test.TestProperties;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link MarySynthesizedOutput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class TestMarySynthesizedOutput
    implements SynthesizedOutputListener {
    
    
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TestMarySynthesizedOutput.class);
    
    /** Delay between to lookups if the Mary server has been started. */
    private static final int DELAY = 1000;

    /** The test object. */
    private MarySynthesizedOutput output;

    /** Event notification mechanism. */
    private final Object lock = new Object();

    /** The Mary process. */
    private static Process process;

    /**
     * Starts the Mary server.
     * @throws Exception
     *         start failed
     */
    @BeforeClass
    public static void init() throws Exception {
        final Runtime runtime = Runtime.getRuntime();
        final TestProperties properties = new TestProperties();
        final String mary = properties.get("mary.startcmd");
       System.out.println(mary);
        process = runtime.exec(mary);
        boolean started = false;
        do {
            Thread.sleep(DELAY);
            try {
                MaryClient.getMaryClient();
                started = true;
            } catch (IOException ignore) {
            }
        } while (!started);
    }

    /**
     * Shutdown of the Mary TTS server.
     */
    @AfterClass
    public static void shutdown() {
        if (process != null) {
            process.destroy();
        }
    }

    /**
     * Set up the test environment.
     */
    @Before
    public void setUp() {
        output = new MarySynthesizedOutput();
        output.setAudioType("WAVE");
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
        speak.setXmlLang(Locale.US);
        speak.addText("hello from SSML");
        final SpeakableSsmlText ssml = new SpeakableSsmlText(doc);
        output.queueSpeakable(ssml, null);
        synchronized (lock) {
            lock.wait();
        }
    }

    
    
    /**
     * Test method for {@link org.jvoicexml.implementation.mary.MarySynthesizedOutput#waitQueueEmpty()}.
     * @exception Exception test failed
     * @exception JVoiceXMLEvent test failed
     */ 
    public void testWaitQueueEmpty() throws Exception, JVoiceXMLEvent {
        
        SpeakablePlainText plainText =
            new SpeakablePlainText("Test 1");
        output.queueSpeakable( plainText, null);
        LOGGER.info(plainText+" offered to queue");
        
        
        plainText=new SpeakablePlainText("Test 2");
        output.queueSpeakable( plainText, null);
        LOGGER.info(plainText+" offered to queue");
             
        
        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("Test 3 from SSML");
        final SpeakableSsmlText ssml = new SpeakableSsmlText(doc);
        output.queueSpeakable(ssml, null);
        LOGGER.info(ssml.getSpeakableText()+" offered to queue");
        
        
        final SsmlDocument doc2 = new SsmlDocument();
        final Speak speak2 = doc2.getSpeak();
        speak2.setXmlLang(Locale.US);
        speak2.addText("Test 4 from SSML");
        final SpeakableSsmlText ssml2 = new SpeakableSsmlText(doc2);
        output.queueSpeakable(ssml2, null);
        LOGGER.info(ssml2.getSpeakableText()+" offered to queue");
        
        
        output.waitQueueEmpty();
        LOGGER.info("returning resources....");
        
    }
    
    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        if (event instanceof OutputEndedEvent) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

}
