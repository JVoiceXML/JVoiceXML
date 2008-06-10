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
package org.jvoicexml.implementation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.CallControl;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.test.DummyRemoteClient;
import org.jvoicexml.test.implementationplatform.DummyAudioFileOutputFactory;
import org.jvoicexml.test.implementationplatform.DummySpokenInputFactory;
import org.jvoicexml.test.implementationplatform.DummySynthesizedOutput;
import org.jvoicexml.test.implementationplatform.DummySynthesizedOutputFactory;

/**
 * Test cases for {@link KeyedResourcePool}.
 * @author Dirk Schnelle
 */
public final class TestJVoiceXmlImplementationPlatform {
    /** Delay in msec before a used resource is returned to the pool. */
    private static final int DELAY_RETURN = 1000;

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
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowSystemOutput() throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        Assert.assertNotNull(output1);
        platform.returnSystemOutput(output1);
        final SystemOutput output2 = platform.borrowSystemOutput();
        Assert.assertNotNull(output2);
        platform.returnSystemOutput(output2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowSystemOutputNoresource()
        throws Exception, NoresourceError {
        synthesizerPool.borrowObject(client.getSystemOutput());
        NoresourceError error = null;
        try {
            platform.borrowSystemOutput();
        } catch (NoresourceError e) {
            error = e;
        }
        Assert.assertNotNull("second call should have failed", error);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowSystemOutputDelayedReturn()
        throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        Assert.assertNotNull(output1);
        final Runnable runnable = new Runnable() {
            public void run() {
                SystemOutput output2 = null;
                try {
                    output2 = platform.borrowSystemOutput();
                } catch (NoresourceError e) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertNotNull(output2);
                platform.returnSystemOutput(output2);
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(DELAY_RETURN);
        platform.returnSystemOutput(output1);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testBorrowSystemOutputBusy()
        throws Exception, JVoiceXMLEvent {
        final SystemOutput output1 = platform.borrowSystemOutput();
        Assert.assertNotNull(output1);
        final Runnable runnable = new Runnable() {
            public void run() {
                SystemOutput output2 = null;
                try {
                    output2 = platform.borrowSystemOutput();
                } catch (NoresourceError e) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertNotNull(output2);
                platform.returnSystemOutput(output2);
            }
        };
        final SpeakableText speakable = new SpeakablePlainText("test");
        output1.queueSpeakable(speakable, false, null);
        platform.returnSystemOutput(output1);
        final Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(DELAY_RETURN);
        SynthesizedOutputProvider provider =
            (SynthesizedOutputProvider) output1;
        DummySynthesizedOutput synthesizer =
            (DummySynthesizedOutput) provider.getSynthesizedOutput();
        synthesizer.outputEnded();
        Thread.sleep(DELAY_RETURN);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowCallControl() throws Exception, NoresourceError {
        final CallControl call1 = platform.borrowCallControl();
        Assert.assertNotNull(call1);
        platform.returnCallControl(call1);
        final CallControl call2 = platform.borrowCallControl();
        Assert.assertNotNull(call2);
        platform.returnCallControl(call2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowCallControlNoresource()
        throws Exception, NoresourceError {
        telephonyPool.borrowObject(client.getCallControl());
        NoresourceError error = null;
        try {
            platform.borrowCallControl();
        } catch (NoresourceError e) {
            error = e;
        }
        Assert.assertNotNull("second call should have failed", error);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowCallControlDelayedReturn()
        throws Exception, NoresourceError {
        final CallControl call1 = platform.borrowCallControl();
        Assert.assertNotNull(call1);
        final Runnable runnable = new Runnable() {
            public void run() {
                CallControl call2 = null;
                try {
                    call2 = platform.borrowCallControl();
                } catch (NoresourceError e) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertNotNull(call2);
                platform.returnCallControl(call2);
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(DELAY_RETURN);
        platform.returnCallControl(call1);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testBorrowCallControlBusy()
        throws Exception, JVoiceXMLEvent {
        final CallControl call1 = platform.borrowCallControl();
        Assert.assertNotNull(call1);
        final Runnable runnable = new Runnable() {
            public void run() {
                CallControl call2 = null;
                try {
                    call2 = platform.borrowCallControl();
                } catch (NoresourceError e) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertNotNull(call2);
                platform.returnCallControl(call2);
            }
        };
        call1.play(null, null);
        platform.returnCallControl(call1);
        final Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(DELAY_RETURN);
        call1.stopPlay();
        Thread.sleep(DELAY_RETURN);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowUserInput() throws Exception, NoresourceError {
        final UserInput input1 = platform.borrowUserInput();
        Assert.assertNotNull(input1);
        platform.returnUserInput(input1);
        final UserInput input2 = platform.borrowUserInput();
        Assert.assertNotNull(input2);
        platform.returnUserInput(input2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowUserInputNoresource()
        throws Exception, NoresourceError {
        recognizerPool.borrowObject(client.getUserInput());
        NoresourceError error = null;
        try {
            platform.borrowUserInput();
        } catch (NoresourceError e) {
            error = e;
        }
        Assert.assertNotNull("second call should have failed", error);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    @Test
    public void testBorrowUserInputDelayedReturn()
        throws Exception, NoresourceError {
        final UserInput input1 = platform.borrowUserInput();
        Assert.assertNotNull(input1);
        final Runnable runnable = new Runnable() {
            public void run() {
                UserInput input2 = null;
                try {
                    input2 = platform.borrowUserInput();
                } catch (NoresourceError e) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertNotNull(input2);
                platform.returnUserInput(input2);
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(DELAY_RETURN);
        platform.returnUserInput(input1);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testBorrowUserInputBusy()
        throws Exception, JVoiceXMLEvent {
        final UserInput input1 = platform.borrowUserInput();
        Assert.assertNotNull(input1);
        final Runnable runnable = new Runnable() {
            public void run() {
                UserInput input2 = null;
                try {
                    input2 = platform.borrowUserInput();
                } catch (NoresourceError e) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertNotNull(input2);
                platform.returnUserInput(input2);
            }
        };
        final Thread thread = new Thread(runnable);
        input1.startRecognition();
        thread.start();
        Thread.sleep(DELAY_RETURN);
        platform.returnUserInput(input1);
        input1.stopRecognition();
        Thread.sleep(DELAY_RETURN);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetBorrowUserInput()
        throws Exception, JVoiceXMLEvent {
        final UserInput input1 = platform.getBorrowedUserInput();
        Assert.assertNull(input1);
        final UserInput input2 = platform.borrowUserInput();
        Assert.assertNotNull(input2);
        final UserInput input3 = platform.getBorrowedUserInput();
        Assert.assertEquals(input2, input3);
        platform.returnUserInput(input2);
        final UserInput input4 = platform.getBorrowedUserInput();
        Assert.assertNull(input4);
    }
}
