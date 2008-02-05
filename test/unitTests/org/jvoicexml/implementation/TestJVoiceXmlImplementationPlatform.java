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

import junit.framework.TestCase;

import org.jvoicexml.CallControl;
import org.jvoicexml.RemoteClient;
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
public final class TestJVoiceXmlImplementationPlatform extends TestCase {
    /** Delay in msec before a used resource is returned to the pool. */
    private static final int DELAY_RETURN = 1000;

    /** The test object. */
    private JVoiceXmlImplementationPlatform platform;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        KeyedResourcePool<SynthesizedOutput> synthesizerPool =
            new KeyedResourcePool<SynthesizedOutput>();
        DummySynthesizedOutputFactory synthesizedOutputFactory =
            new DummySynthesizedOutputFactory();
        synthesizedOutputFactory.setInstances(1);
        synthesizerPool.addResourceFactory(synthesizedOutputFactory);
        KeyedResourcePool<AudioFileOutput> fileOutputPool =
            new KeyedResourcePool<AudioFileOutput>();
        DummyAudioFileOutputFactory audioFileOutputFactory =
            new DummyAudioFileOutputFactory();
        audioFileOutputFactory.setInstances(1);
        fileOutputPool.addResourceFactory(audioFileOutputFactory);
        KeyedResourcePool<Telephony> telephonyPool =
            new KeyedResourcePool<Telephony>();
        DummyTelephonySupportFactory telephonyFactory =
            new DummyTelephonySupportFactory();
        telephonyFactory.setInstances(1);
        telephonyPool.addResourceFactory(telephonyFactory);
        KeyedResourcePool<SpokenInput> recognizerPool =
            new KeyedResourcePool<SpokenInput>();
        DummySpokenInputFactory spokenInputFactory =
            new DummySpokenInputFactory();
        spokenInputFactory.setInstances(1);
        recognizerPool.addResourceFactory(spokenInputFactory);
        RemoteClient client = new DummyRemoteClient();
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
    public void testBorrowSystemOutput() throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        assertNotNull(output1);
        platform.returnSystemOutput(output1);
        final SystemOutput output2 = platform.borrowSystemOutput();
        assertNotNull(output2);
        platform.returnSystemOutput(output2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowSystemOutputNoresource()
        throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        assertNotNull(output1);
        NoresourceError error = null;
        try {
            platform.borrowSystemOutput();
        } catch (NoresourceError e) {
            error = e;
        }
        assertNotNull("second call should have failed", error);
        platform.returnSystemOutput(output1);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowSystemOutputDelayedReturn()
        throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        assertNotNull(output1);
        final Runnable runnable = new Runnable() {
            public void run() {
                SystemOutput output2 = null;
                try {
                    output2 = platform.borrowSystemOutput();
                } catch (NoresourceError e) {
                   fail(e.getMessage());
                }
                assertNotNull(output2);
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
    public void testBorrowSystemOutputBusy()
        throws Exception, JVoiceXMLEvent {
        final SystemOutput output1 = platform.borrowSystemOutput();
        assertNotNull(output1);
        final Runnable runnable = new Runnable() {
            public void run() {
                SystemOutput output2 = null;
                try {
                    output2 = platform.borrowSystemOutput();
                } catch (NoresourceError e) {
                   fail(e.getMessage());
                }
                assertNotNull(output2);
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
    public void testBorrowCallControl() throws Exception, NoresourceError {
        final CallControl call1 = platform.borrowCallControl();
        assertNotNull(call1);
        platform.returnCallControl(call1);
        final CallControl call2 = platform.borrowCallControl();
        assertNotNull(call2);
        platform.returnCallControl(call2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowCallControlNoresource()
        throws Exception, NoresourceError {
        final CallControl call1 = platform.borrowCallControl();
        assertNotNull(call1);
        NoresourceError error = null;
        try {
            platform.borrowCallControl();
        } catch (NoresourceError e) {
            error = e;
        }
        assertNotNull("second call should have failed", error);
        platform.returnCallControl(call1);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowCallControlDelayedReturn()
        throws Exception, NoresourceError {
        final CallControl call1 = platform.borrowCallControl();
        assertNotNull(call1);
        final Runnable runnable = new Runnable() {
            public void run() {
                CallControl call2 = null;
                try {
                    call2 = platform.borrowCallControl();
                } catch (NoresourceError e) {
                   fail(e.getMessage());
                }
                assertNotNull(call2);
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
    public void testBorrowCallControlBusy()
        throws Exception, JVoiceXMLEvent {
        final CallControl call1 = platform.borrowCallControl();
        assertNotNull(call1);
        final Runnable runnable = new Runnable() {
            public void run() {
                CallControl call2 = null;
                try {
                    call2 = platform.borrowCallControl();
                } catch (NoresourceError e) {
                   fail(e.getMessage());
                }
                assertNotNull(call2);
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
    public void testBorrowUserInput() throws Exception, NoresourceError {
        final UserInput input1 = platform.borrowUserInput();
        assertNotNull(input1);
        platform.returnUserInput(input1);
        final UserInput input2 = platform.borrowUserInput();
        assertNotNull(input2);
        platform.returnUserInput(input2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowUserInputNoresource()
        throws Exception, NoresourceError {
        final UserInput input1 = platform.borrowUserInput();
        assertNotNull(input1);
        NoresourceError error = null;
        try {
            platform.borrowUserInput();
        } catch (NoresourceError e) {
            error = e;
        }
        assertNotNull("second call should have failed", error);
        platform.returnUserInput(input1);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowCallControl()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowUserInputDelayedReturn()
        throws Exception, NoresourceError {
        final UserInput input1 = platform.borrowUserInput();
        assertNotNull(input1);
        final Runnable runnable = new Runnable() {
            public void run() {
                UserInput input2 = null;
                try {
                    input2 = platform.borrowUserInput();
                } catch (NoresourceError e) {
                   fail(e.getMessage());
                }
                assertNotNull(input2);
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
    public void testBorrowUserInputBusy()
        throws Exception, JVoiceXMLEvent {
        final UserInput input1 = platform.borrowUserInput();
        assertNotNull(input1);
        final Runnable runnable = new Runnable() {
            public void run() {
                UserInput input2 = null;
                try {
                    input2 = platform.borrowUserInput();
                } catch (NoresourceError e) {
                   fail(e.getMessage());
                }
                assertNotNull(input2);
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
    public void testGetBorrowUserInput()
        throws Exception, JVoiceXMLEvent {
        final UserInput input1 = platform.getBorrowedUserInput();
        assertNull(input1);
        final UserInput input2 = platform.borrowUserInput();
        assertNotNull(input2);
        final UserInput input3 = platform.getBorrowedUserInput();
        assertEquals(input2, input3);
        platform.returnUserInput(input2);
        final UserInput input4 = platform.getBorrowedUserInput();
        assertNull(input4);
    }
}
