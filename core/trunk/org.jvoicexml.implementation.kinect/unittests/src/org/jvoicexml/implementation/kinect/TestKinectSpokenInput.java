/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.test.implementation.DummySpokenInputListener;

/**
 * Tests for {@link KinectSpokenInput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class TestKinectSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TestKinectRecognizer.class);

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#getType()}.
     */
    @Test
    public void testGetType() {
        final KinectSpokenInput input = new KinectSpokenInput();
        input.setType("kinect");
        Assert.assertEquals("kinect", input.getType());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#open()}.
     * @throws JVoiceXMLEvent test failed
     */
    @Test
    public void testOpen() throws JVoiceXMLEvent {
        final KinectSpokenInput input = new KinectSpokenInput();
        input.open();
        Assert.assertTrue(input.isOpen());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#activate()}.
     * @throws JVoiceXMLEvent test failed
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
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#startRecognition(org.jvoicexml.SpeechRecognizerProperties, org.jvoicexml.DtmfRecognizerProperties)}.
     * @throws JVoiceXMLEvent test failed
     * @throws Exception test failed
     */
    @Test
    public void testStartRecognition() throws JVoiceXMLEvent, Exception {
        final KinectSpokenInput input = new KinectSpokenInput();
        final DummySpokenInputListener listener =
                new DummySpokenInputListener();
        input.addListener(listener);
        input.open();
        input.activate();
        input.startRecognition(null, null);
        Assert.assertTrue(input.isBusy());
        listener.waitSize(1, 10000);
        final SpokenInputEvent event1 = listener.get(0);
        final int type1 = event1.getEvent();
        Assert.assertEquals(SpokenInputEvent.RECOGNITION_STARTED, type1);
        LOGGER.info("Say something!");
        listener.waitSize(2, 10000);
        final SpokenInputEvent event2 = listener.get(1);
        final int type2 = event2.getEvent();
        Assert.assertEquals(SpokenInputEvent.RESULT_ACCEPTED, type2);
        input.passivate();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectSpokenInput#stopRecognition()}.
     * @throws JVoiceXMLEvent test failed
     * @throws Exception test failed
     */
    @Test
    public void testStopRecognition() throws Exception, JVoiceXMLEvent {
        final KinectSpokenInput input = new KinectSpokenInput();
        final DummySpokenInputListener listener =
                new DummySpokenInputListener();
        input.addListener(listener);
        input.open();
        input.activate();
        input.startRecognition(null, null);
        listener.waitSize(1, 10000);
        final SpokenInputEvent event1 = listener.get(0);
        final int type1 = event1.getEvent();
        Assert.assertEquals(SpokenInputEvent.RECOGNITION_STARTED, type1);
        LOGGER.info("Say nothing");
        Thread.sleep(5000);
        input.stopRecognition();
        Assert.assertFalse(input.isBusy());
        input.passivate();
    }
}
