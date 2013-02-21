/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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
package org.jvoicexml.implementation.jvxml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.CallControl;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.pool.KeyedResourcePool;
import org.jvoicexml.test.DummyRemoteClient;
import org.jvoicexml.test.implementation.DummyAudioFileOutputFactory;
import org.jvoicexml.test.implementation.DummySpokenInputFactory;
import org.jvoicexml.test.implementation.DummySynthesizedOutputFactory;

/**
 * Test cases for {@link KeyedResourcePool}.
 * @author Dirk Schnelle
 */
public final class TestJVoiceXmlImplementationPlatform {
    /** The test object. */
    private JVoiceXmlImplementationPlatform platform;

    /** The synthesizer pool. */
    private KeyedResourcePool<SynthesizedOutput> synthesizerPool;

    /** The file output pool. */
    private KeyedResourcePool<AudioFileOutput> fileOutputPool;

    /** The telephony pool. */
    private KeyedResourcePool<Telephony> telephonyPool;

    /** The recognizer pool. */
    private KeyedResourcePool<SpokenInput> recognizerPool;

    /** The remote client configuration. */
    private RemoteClient client;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        synthesizerPool = new KeyedResourcePool<SynthesizedOutput>();
        DummySynthesizedOutputFactory synthesizedOutputFactory =
            new DummySynthesizedOutputFactory();
        synthesizedOutputFactory.setInstances(1);
        synthesizerPool.addResourceFactory(synthesizedOutputFactory);
        fileOutputPool = new KeyedResourcePool<AudioFileOutput>();
        DummyAudioFileOutputFactory audioFileOutputFactory =
            new DummyAudioFileOutputFactory();
        audioFileOutputFactory.setInstances(1);
        fileOutputPool.addResourceFactory(audioFileOutputFactory);
        telephonyPool = new KeyedResourcePool<Telephony>();
        DummyTelephonySupportFactory telephonyFactory =
            new DummyTelephonySupportFactory();
        telephonyFactory.setInstances(1);
        telephonyPool.addResourceFactory(telephonyFactory);
        recognizerPool = new KeyedResourcePool<SpokenInput>();
        DummySpokenInputFactory spokenInputFactory =
            new DummySpokenInputFactory();
        spokenInputFactory.setInstances(1);
        recognizerPool.addResourceFactory(spokenInputFactory);
        client = new DummyRemoteClient();
        platform = new JVoiceXmlImplementationPlatform(telephonyPool,
                synthesizerPool, fileOutputPool, recognizerPool, client);
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

}
