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

import static org.junit.Assert.*;

import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Test cases for {@link KinectRecognizer}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3350 $
 * @since 0.7.6
 */
public final class TestKinectRecognizer {

    @Test
    public void testAllocate() throws JVoiceXMLEvent {
        final KinectRecognizer recognizer = new KinectRecognizer();
        recognizer.allocate();
    }

    @Test
    public void testStartRecognition() {
        fail("Not yet implemented");
    }

    @Test
    public void testStopRecognition() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeallocate() {
        fail("Not yet implemented");
    }

}
