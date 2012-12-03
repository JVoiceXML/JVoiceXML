/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.kinect/src/org/jvoicexml/implementation/kinect/KinectRecognizer.java $
 * Version: $LastChangedRevision: 3350 $
 * Date:    $Date: 2012-11-28 17:48:42 +0100 (Mi, 28 Nov 2012) $
 * Author:  $LastChangedBy: schnelle $
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
import org.jvoicexml.test.implementation.DummySpokenInputListener;

/**
 * Test cases for {@link KinectRecognizer}.
 * <p>
 * Make sure that a Kinect is attached to your computer when running these
 * tests.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3350 $
 * @since 0.7.6
 */
public final class TestKinectRecognizer {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TestKinectRecognizer.class);
    
    /**
     * Test method for {@link KinectRecognizer#allocate()}.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testAllocate() throws Exception {
        final KinectRecognizer recognizer = new KinectRecognizer(null);
        recognizer.allocate();
        Assert.assertTrue(recognizer.isAllocated());
    }

    /**
     * Test method for {@link KinectRecognizer#deallocate()}.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testDeallocate() throws Exception {
        final KinectRecognizer recognizer = new KinectRecognizer(null);
        recognizer.allocate();
        Assert.assertTrue(recognizer.isAllocated());
        recognizer.deallocate();
        Assert.assertFalse(recognizer.isAllocated());
    }

    
    @Test
    public void testStartRecognition() throws Exception {
        final KinectSpokenInput input = new KinectSpokenInput();
        final DummySpokenInputListener listener =
                new DummySpokenInputListener();
        input.addListener(listener);
        final KinectRecognizer recognizer = new KinectRecognizer(input);
        recognizer.allocate();
        recognizer.startRecognition();
        LOGGER.info("Say something!");
        listener.waitSize(1, 10000);
    }

    @Test
    public void testStopRecognition() {
        fail("Not yet implemented");
    }

}
