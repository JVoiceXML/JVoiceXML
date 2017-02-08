/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.kinect;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStartedEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.mock.implementation.MockSpokenInputListener;

/**
 * Tests for {@link KinectSpokenInput}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class TestKinectSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TestKinectRecognizer.class);

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#getType()}.
     */
    @Test
    public void testGetType() {
        final KinectSpokenInput input = new KinectSpokenInput();
        input.setType("kinect");
        Assert.assertEquals("kinect", input.getType());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#open()}.
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test
    public void testOpen() throws JVoiceXMLEvent {
        final KinectSpokenInput input = new KinectSpokenInput();
        input.open();
        Assert.assertTrue(input.isOpen());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#activate()}.
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test
    public void testActivate() throws JVoiceXMLEvent {
        final KinectSpokenInput input = new KinectSpokenInput();
        input.open();
        input.activate();
        Assert.assertTrue(input.isActivated());
        input.passivate();
        Assert.assertFalse(input.isActivated());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#startRecognition(org.jvoicexml.SpeechRecognizerProperties, org.jvoicexml.DtmfRecognizerProperties)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @throws Exception
     *             test failed
     */
    @Test
    public void testStartRecognition() throws JVoiceXMLEvent, Exception {
        final KinectSpokenInput input = new KinectSpokenInput();
        final MockSpokenInputListener listener = new MockSpokenInputListener();
        input.addListener(listener);
        input.open();
        input.activate();
        input.startRecognition(null, null, null);
        Assert.assertTrue(input.isBusy());
        listener.waitSize(1, 10000);
        final SpokenInputEvent event1 = listener.get(0);
        Assert.assertEquals(RecognitionStartedEvent.EVENT_TYPE,
                event1.getEventType());
        LOGGER.info("Say something!");
        listener.waitSize(2, 10000);
        final SpokenInputEvent event2 = listener.get(1);
        Assert.assertEquals(RecognitionEvent.EVENT_TYPE, event2.getEventType());
        input.passivate();
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#stopRecognition()}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @throws Exception
     *             test failed
     */
    @Test
    public void testStopRecognition() throws Exception, JVoiceXMLEvent {
        final KinectSpokenInput input = new KinectSpokenInput();
        final MockSpokenInputListener listener = new MockSpokenInputListener();
        input.addListener(listener);
        input.open();
        input.activate();
        input.startRecognition(null, null, null);
        listener.waitSize(1, 10000);
        final SpokenInputEvent event1 = listener.get(0);
        Assert.assertEquals(RecognitionEvent.EVENT_TYPE, event1.getEventType());
        LOGGER.info("Say nothing");
        Thread.sleep(5000);
        input.stopRecognition();
        Assert.assertFalse(input.isBusy());
        input.passivate();
    }
}
