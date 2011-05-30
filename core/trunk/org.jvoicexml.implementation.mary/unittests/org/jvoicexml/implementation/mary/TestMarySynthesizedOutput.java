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

import java.io.InputStream;
import java.util.Locale;

import marytts.client.MaryClient;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.test.TestProperties;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;

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

    /** Output Ended  notification mechanism. */
    private final Object outputEndedLock = new Object();

    /** Output Started notification mechanism. */
    private final Object outputStartedLock = new Object();

    /**Flag that indicates if the current output has ended. */
    private boolean outputEnded;

    /**Flag that indicates if the current output has started. */
    private boolean outputStarted;
       
    /** The Mary process. */
    private static Process process;

    /** The input gobbler. */
    private static StreamGobbler ingobbler;

    /** The error gobbler. */
    private static StreamGobbler errgobbler;

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
        LOGGER.info("starting '" + mary + "'...");
        process = runtime.exec(mary);
        final InputStream in = process.getInputStream();
        ingobbler = new StreamGobbler(in, System.out);
        ingobbler.start();
        final InputStream err = process.getErrorStream();
        errgobbler = new StreamGobbler(err, System.out);
        errgobbler.start();
        
        boolean started = false;
        do {
            Thread.sleep(DELAY);
            try {
                MaryClient.getMaryClient();
                started = true;
            } catch (Exception ignore) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("not started yet, retrying in " + DELAY
                            + " msecs...");
                }
            }
        } while (!started);
        LOGGER.info("...started");
    }

    /**
     * Shutdown of the Mary TTS server.
     */
    @AfterClass
    public static void shutdown() {
        if (process != null) {
            LOGGER.info("shutdown Mary...");
            process.destroy();
            ingobbler.stopGobbling();
            errgobbler.stopGobbling();
            LOGGER.info("...shutdown completed");
        }
    }

    /**
     * Set up the test environment.
     * @exception Exception
     *            setup failed
     */
    @Before
    public void setUp() throws Exception {
        output = new MarySynthesizedOutput();
        output.setAudioType("WAVE");
        output.setLang("en-US");
        output.setVoiceName("cmu-slt-hsmm");
        output.addListener(this);
        output.activate();
        output.connect(null);
    }

    /**
     * Tear down the test environment.
     */
    public void tearDown() {
        output.removeListener(this);
        output.disconnect(null);
        output.passivate();
        output = null;
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
        synchronized (outputEndedLock) {
            outputEndedLock.wait();
        }
        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("hello from SSML");
        final SpeakableSsmlText ssml = new SpeakableSsmlText(doc);
        output.queueSpeakable(ssml, null);
        synchronized (outputEndedLock) {
            outputEndedLock.wait();
        }
    }

    
    
    /**
     * Test method for {@link org.jvoicexml.implementation.mary.MarySynthesizedOutput#waitQueueEmpty()}.
     * @exception Exception test failed
     * @exception JVoiceXMLEvent test failed
     */ 
    @Test(timeout = 20000)
    public void testWaitQueueEmpty() throws Exception, JVoiceXMLEvent {
        
        final SsmlDocument doc1 = new SsmlDocument();
        final Speak speak1 = doc1.getSpeak();
        speak1.setXmlLang(Locale.US);
        speak1.addText("Test 1 from SSML");
        final SpeakableSsmlText ssml1 =
            new SpeakableSsmlText(doc1, true, BargeInType.SPEECH);

        final SsmlDocument doc2 = new SsmlDocument();
        final Speak speak2 = doc2.getSpeak();
        speak2.setXmlLang(Locale.US);
        speak2.addText("Test 2 from SSML");
        final SpeakableSsmlText ssml2 =
            new SpeakableSsmlText(doc2, true, BargeInType.SPEECH);
        
        SpeakablePlainText plainText =
            new SpeakablePlainText("Test 3");
        
        SpeakablePlainText plainText2 =
            new SpeakablePlainText("Test 4");
        
        final SsmlDocument doc3 = new SsmlDocument();
        final Speak speak3 = doc3.getSpeak();
        speak3.setXmlLang(Locale.US);
        speak3.addText("Test 5 from SSML");
        final SpeakableSsmlText ssml3 =
            new SpeakableSsmlText(doc3, true, BargeInType.SPEECH);

        output.queueSpeakable(ssml1, null);
        output.queueSpeakable(ssml2, null);
        output.queueSpeakable(plainText, null);
        output.queueSpeakable(plainText2, null);
        output.queueSpeakable(ssml3, null);

        LOGGER.info("Speakables offered to the synthesisQueue");

        output.waitQueueEmpty();

        LOGGER.info("Return resources...");
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.mary.MarySynthesizedOutput#cancelOutput()}.
     * @exception Exception test failed
     * @exception JVoiceXMLEvent test failed
     */  
    @Test(timeout = 20000)
    public void testCancelOutput() throws Exception, JVoiceXMLEvent {
        final SsmlDocument doc1 = new SsmlDocument();
        final Speak speak1 = doc1.getSpeak();
        speak1.setXmlLang(Locale.US);
        speak1.addText("Test 1.Barge-in on.");
        final SpeakableSsmlText ssml1 =
            new SpeakableSsmlText(doc1, true, BargeInType.SPEECH);
        output.queueSpeakable(ssml1, null);
        LOGGER.info(ssml1.getSpeakableText() + " offered to queue");
        synchronized (outputStartedLock) {
            while (!outputStarted) {
                outputStartedLock.wait();
            }
        }
        Thread.sleep(DELAY);
        output.cancelOutput();
        synchronized (outputEndedLock) {
            while (!outputEnded) {
                outputEndedLock.wait();
            }
        }
        final SsmlDocument doc2 = new SsmlDocument();
        final Speak speak2 = doc2.getSpeak();
        speak2.setXmlLang(Locale.US);
        speak2.addText("Test 2.Barge-in on.");
        final SpeakableSsmlText ssml2 =
            new SpeakableSsmlText(doc2, true, BargeInType.SPEECH);
        output.queueSpeakable(ssml2, null);
        LOGGER.info(ssml2.getSpeakableText() + " offered to queue");
       
        synchronized (outputStartedLock) {
            while (!outputStarted) {
                outputStartedLock.wait();
            }
        }
        Thread.sleep(DELAY);
        output.cancelOutput();
        synchronized (outputEndedLock) {
            while (!outputEnded) {
                outputEndedLock.wait();
            }
        } 

        SpeakablePlainText plainText = new SpeakablePlainText(
                "Test 3.Barge-in off.You can not skip this audio");
        output.queueSpeakable(plainText, null);
        LOGGER.info(plainText + " offered to queue");
        synchronized (outputStartedLock) {
            while (!outputStarted) {
                outputStartedLock.wait();
            }
        }
        Thread.sleep(DELAY);
        output.cancelOutput();

        synchronized (outputEndedLock) {
            while (!outputEnded) { 
                outputEndedLock.wait();
            }
        } 

        plainText = new SpeakablePlainText(
                "Test 4.Barge-in off.You can not skip this audio");
        output.queueSpeakable(plainText, null);
        LOGGER.info(plainText + " offered to queue");
        synchronized (outputStartedLock) {
            while (!outputStarted) {
                outputStartedLock.wait();
            }

        }     
        Thread.sleep(DELAY);
        output.cancelOutput();
        synchronized (outputEndedLock) {
            while (!outputEnded) {
                outputEndedLock.wait();
            }
        } 

        final SsmlDocument doc3 = new SsmlDocument();
        final Speak speak3 = doc3.getSpeak();
        speak3.setXmlLang(Locale.US);
        speak3.addText("Test 5.Barge-in on. ");
        final SpeakableSsmlText ssml3 =
            new SpeakableSsmlText(doc3, true, BargeInType.SPEECH);
        output.queueSpeakable(ssml3, null);
        LOGGER.info(ssml3.getSpeakableText() + " offered to queue");
        synchronized (outputStartedLock) {
            while (!outputStarted) {
                outputStartedLock.wait();
            }
        }
        Thread.sleep(DELAY);
        output.cancelOutput();
              
        synchronized (outputEndedLock) {
            while (!outputEnded) {
                outputEndedLock.wait();
            }
        } 

        plainText = new SpeakablePlainText(
                "Test 6.Barge-in off.You can not skip this audio");
        output.queueSpeakable(plainText, null);
        LOGGER.info(plainText + " offered to queue");
        synchronized (outputStartedLock) {
            while (!outputStarted) {
                outputStartedLock.wait();
            }
        }     
        Thread.sleep(DELAY);
        output.cancelOutput();
        synchronized (outputEndedLock) {
            while (!outputEnded) { 
                outputEndedLock.wait();
            }
        } 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        if (event instanceof OutputEndedEvent) {
            synchronized (outputEndedLock) {
                outputEnded = true;
                outputStarted = false;
                outputEndedLock.notifyAll();
            }
        }    
        if (event instanceof OutputStartedEvent) {
            synchronized (outputStartedLock) {
                outputStarted = true;
                outputEnded = false;
                outputStartedLock.notifyAll();
            }
        }     
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputError(final ErrorEvent error) {
    }
}

