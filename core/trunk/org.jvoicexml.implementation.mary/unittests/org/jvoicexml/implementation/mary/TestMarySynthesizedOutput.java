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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import marytts.client.MaryClient;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.OutputEndedEvent;
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
        
        final SsmlDocument doc1 = new SsmlDocument();
        final Speak speak1 = doc1.getSpeak();
        speak1.setXmlLang(Locale.US);
        speak1.addText("Test 1 from SSML");
        final SpeakableSsmlText ssml1 = new SpeakableSsmlText(doc1,true, BargeInType.SPEECH);

        final SsmlDocument doc2 = new SsmlDocument();
        final Speak speak2 = doc2.getSpeak();
        speak2.setXmlLang(Locale.US);
        speak2.addText("Test 2 from SSML");
        final SpeakableSsmlText ssml2 = new SpeakableSsmlText(doc2,true, BargeInType.SPEECH);
        
        SpeakablePlainText plainText =
            new SpeakablePlainText("Test 3");
        
        SpeakablePlainText plainText2 =
            new SpeakablePlainText("Test 4");
        
        final SsmlDocument doc3 = new SsmlDocument();
        final Speak speak3 = doc3.getSpeak();
        speak3.setXmlLang(Locale.US);
        speak3.addText("Test 5 from SSML");
        final SpeakableSsmlText ssml3 = new SpeakableSsmlText(doc3,true, BargeInType.SPEECH);
        
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
    public void testCancelOutput() throws Exception, JVoiceXMLEvent {
       
        TestGui testGui = new TestGui();
        
        final SsmlDocument doc1 = new SsmlDocument();
        final Speak speak1 = doc1.getSpeak();
        speak1.setXmlLang(Locale.US);
        speak1.addText("Test 1.Barge-in on. Push the cancel button to skip this audio");
        final SpeakableSsmlText ssml1 = new SpeakableSsmlText(doc1,true, BargeInType.SPEECH);
        output.queueSpeakable(ssml1, null);
        LOGGER.info(ssml1.getSpeakableText()+" offered to queue");

        final SsmlDocument doc2 = new SsmlDocument();
        final Speak speak2 = doc2.getSpeak();
        speak2.setXmlLang(Locale.US);
        speak2.addText("Test 2.Barge-in on. Push the cancel button to skip this audio");
        final SpeakableSsmlText ssml2 = new SpeakableSsmlText(doc2,true, BargeInType.SPEECH);
        output.queueSpeakable(ssml2, null);
        LOGGER.info(ssml2.getSpeakableText()+" offered to queue");
        
            
        final SsmlDocument doc3 = new SsmlDocument();
        final Speak speak3 = doc3.getSpeak();
        speak3.setXmlLang(Locale.US);
        speak3.addText("Test 3.Barge-in on. Push the cancel button to skip this audio");
        final SpeakableSsmlText ssml3 = new SpeakableSsmlText(doc3,true, BargeInType.SPEECH);
        output.queueSpeakable(ssml3, null);
        LOGGER.info(ssml3.getSpeakableText()+" offered to queue");
        
        
        SpeakablePlainText plainText =
            new SpeakablePlainText("Test 4.Barge-in off.You can not skip this audio");
        output.queueSpeakable( plainText, null);
        LOGGER.info(plainText+" offered to queue");
        
        
        plainText=new SpeakablePlainText("Test 5.Barge-in off.You can not skip this audio");
        output.queueSpeakable( plainText, null);
        LOGGER.info(plainText+" offered to queue");
             
        
        final SsmlDocument doc4 = new SsmlDocument();
        final Speak speak4 = doc4.getSpeak();
        speak4.setXmlLang(Locale.US);
        speak4.addText("Test 6.Barge-in on. Push the cancel button to skip this audio");
        final SpeakableSsmlText ssml4 = new SpeakableSsmlText(doc4,true, BargeInType.HOTWORD);
        output.queueSpeakable(ssml4, null);
        LOGGER.info(ssml4.getSpeakableText()+" offered to queue");
        
        
        final SsmlDocument doc5 = new SsmlDocument();
        final Speak speak5 = doc5.getSpeak();
        speak5.setXmlLang(Locale.US);
        speak5.addText("Test 7.Barge-in on. Push the cancel button to skip this audio");
        final SpeakableSsmlText ssml5 = new SpeakableSsmlText(doc5,true, BargeInType.SPEECH);
        output.queueSpeakable(ssml5, null);
        LOGGER.info(ssml5.getSpeakableText()+" offered to queue");
        
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
    
    public void cancelOutput(){
        
        output.cancelOutput();
        
    }
    
    public void cancelAudioOutput(){
        
        try {
            output.cancelAudioOutput();
        } catch (NoresourceError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
 
     /**A simple GUI for Testing cancelOutput method.*/
    public class TestGui extends Frame implements ActionListener{
        
        JFrame frame;
        JPanel panel;
        JButton cancelOutput;
        
      public TestGui(){
          JFrame.setDefaultLookAndFeelDecorated(true);
          frame = new JFrame("Cancel Output");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setSize(new Dimension(180, 80));

          panel = new JPanel(new GridLayout(3, 2));
          panel.setBorder(BorderFactory.createEmptyBorder(60,
          60,
          20,
          60)
          );
          addItems();
          frame.getRootPane().setDefaultButton(cancelOutput);
          frame.getContentPane().add(panel, BorderLayout.CENTER);
          frame.pack();
          frame.setVisible(true);
          }  
      
      
      
      private void addItems(){
          
         cancelOutput = new JButton("cancelOutput");
         cancelOutput.addActionListener(this);
         panel.add(cancelOutput);

      }



    @Override
    public void actionPerformed(ActionEvent e) {
       if(((JButton)e.getSource()).equals(cancelOutput)){
            LOGGER.info("Cancel Output Requested");
            cancelAudioOutput();
            
        }
        
    }
    
    
    
  }  
        
    
}






