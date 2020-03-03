/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jvxml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.CallControl;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.pool.KeyedResourcePool;
import org.jvoicexml.mock.MockConnectionInformation;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mockito.Mockito;

/**
 * Test cases for {@link KeyedResourcePool}.
 * @author Dirk Schnelle-Walka
 */
public final class TestJVoiceXmlImplementationPlatform {
    /** The test object. */
    private JVoiceXmlImplementationPlatform platform;

    /** The synthesizer pool. */
    private KeyedResourcePool<SynthesizedOutput> synthesizerPool;

    /** The telephony pool. */
    private KeyedResourcePool<Telephony> telephonyPool;

    /** The recognizer pool. */
    private KeyedResourcePool<SpokenInput> recognizerPool;

    /** The connection information to use. */
    private ConnectionInformation info;

    /**
     * {@inheritDoc}
     * @throws NoresourceError test failed
     */
    @Before
    public void setUp() throws Exception, NoresourceError {
        synthesizerPool = new KeyedResourcePool<SynthesizedOutput>();
        @SuppressWarnings("unchecked")
        final ResourceFactory<SynthesizedOutput> synthesizedOutputFactory =
            Mockito.mock(ResourceFactory.class);
        Mockito.when(synthesizedOutputFactory.getInstances()).thenReturn(1);
        Mockito.when(synthesizedOutputFactory.getType()).thenReturn("dummy");
        final SynthesizedOutput output = Mockito.mock(SynthesizedOutput.class);
        Mockito.when(output.getType()).thenReturn("dummy");
        Mockito.when(synthesizedOutputFactory.createResource()).thenReturn(output);
        synthesizerPool.addResourceFactory(synthesizedOutputFactory);
        telephonyPool = new KeyedResourcePool<Telephony>();
        final DesktopTelephonySupportFactory telephonyFactory =
            new DesktopTelephonySupportFactory();
        telephonyFactory.setInstances(1);
        telephonyPool.addResourceFactory(telephonyFactory);
        recognizerPool = new KeyedResourcePool<SpokenInput>();
        @SuppressWarnings("unchecked")
        final ResourceFactory<SpokenInput> spokenInputFactory =
            Mockito.mock(ResourceFactory.class);
        Mockito.when(spokenInputFactory.getInstances()).thenReturn(1);
        final SpokenInput input = Mockito.mock(SpokenInput.class);
        Mockito.when(spokenInputFactory.createResource()).thenReturn(input);
        Mockito.when(spokenInputFactory.getType()).thenReturn("dummy");
        recognizerPool.addResourceFactory(spokenInputFactory);
        info = new MockConnectionInformation(telephonyFactory.getType());
        platform = new JVoiceXmlImplementationPlatform(telephonyPool,
                synthesizerPool, recognizerPool, info);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jvxml.JVoiceXmlImplementationPlatform#getSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetSystemOutput() throws Exception, JVoiceXMLEvent {
        final SystemOutput output1 = platform.getSystemOutput();
        Assert.assertNotNull(output1);
        final SystemOutput output2 = platform.getSystemOutput();
        Assert.assertNotNull(output2);
        Assert.assertEquals(output1, output2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jvxml.JVoiceXmlImplementationPlatform#getCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetCallControl() throws Exception, JVoiceXMLEvent {
        final CallControl call1 = platform.getCallControl();
        Assert.assertNotNull(call1);
        final CallControl call2 = platform.getCallControl();
        Assert.assertNotNull(call2);
        Assert.assertEquals(call1, call2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jvxml.JVoiceXmlImplementationPlatform#getCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testBorrowUserInput() throws Exception, JVoiceXMLEvent {
        final UserInput input1 = platform.getUserInput();
        Assert.assertNotNull(input1);
        final UserInput input2 = platform.getUserInput();
        Assert.assertNotNull(input2);
        Assert.assertEquals(input1, input2);
    }

    /**
     * Test method for {@link JVoiceXmlImplementationPlatform#isUserInputActive()}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * 
     * @since 0.7.3
     */
    @Test
    public void testHasUserInput() throws JVoiceXMLEvent {
       Assert.assertFalse(platform.isUserInputActive());
       platform.getUserInput();
       Assert.assertTrue(platform.isUserInputActive());
    }

    /**
     * Test method for {@link JVoiceXmlImplementationPlatform#waitNonBargeInPlayed()}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * @exception Exception
     *            test failed
     * @since 0.7.3
     */
    @Test(timeout = 5000)
    public void testWaitNonBargeInPlayed() throws JVoiceXMLEvent, Exception {
        final SystemOutput output = platform.getSystemOutput();
        final SsmlDocument doc1 = new SsmlDocument();
        final Speak speak1 = doc1.getSpeak();
        speak1.addText("Test1");
        final SpeakableText text1 =
            new SpeakableSsmlText(doc1, true, BargeInType.HOTWORD);
        final SsmlDocument doc2 = new SsmlDocument();
        final Speak speak2 = doc2.getSpeak();
        speak2.addText("Test2");
        final SpeakableText text2 =
            new SpeakableSsmlText(doc2, true, BargeInType.SPEECH);
        final SsmlDocument doc3 = new SsmlDocument();
        final Speak speak3 = doc3.getSpeak();
        speak3.addText("Test3");
        final SpeakableText text3 =
            new SpeakableSsmlText(doc3);
        output.queueSpeakable(text1, null, null);
        output.queueSpeakable(text2, null, null);
        output.queueSpeakable(text3, null, null);
        platform.waitNonBargeInPlayed();
    }

    /**
     * Test case for {@link JVoiceXmlImplementationPlatform#telephonyCallHungup(org.jvoicexml.implementation.TelephonyEvent)}
     * 
     * @since 0.7.9
     */
    @Test
    public void testTelephonyCallHungup() {
        final Telephony telephony = Mockito.mock(Telephony.class);
        final TelephonyEvent event =
                new TelephonyEvent(telephony, TelephonyEvent.HUNGUP);
        platform.telephonyCallHungup(event);
        Assert.assertFalse(platform.isUserInputActive());
        Assert.assertTrue(platform.isHungup());
        Assert.assertFalse(platform.isClosed());
    }

    /**
     * Test case for {@link JVoiceXmlImplementationPlatform#telephonyCallHungup(org.jvoicexml.implementation.TelephonyEvent)}
     * @throws JVoiceXMLEvent test failed
     * 
     * @since 0.7.9
     */
    @Test
    public void testTelephonyCallHungupOutputActive() throws NoresourceError, JVoiceXMLEvent {
        final Telephony telephony = Mockito.mock(Telephony.class);
        final JVoiceXmlSystemOutput output = (JVoiceXmlSystemOutput) platform.getSystemOutput();
        Mockito.when(output.isBusy()).thenReturn(true);
        Assert.assertNotNull(output);
        final TelephonyEvent event =
                new TelephonyEvent(telephony, TelephonyEvent.HUNGUP);
        platform.telephonyCallHungup(event);
        Assert.assertFalse(platform.isUserInputActive());
        Assert.assertTrue(platform.isHungup());
        Assert.assertFalse(platform.isClosed());
    }
}
