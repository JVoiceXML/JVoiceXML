/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.event.plain.jvxml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.test.DummyRecognitionResult;

/**
 * Test case for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 *
 */
public final class TestRecognitionEvent {
    /** A test result. */
    private DummyRecognitionResult result;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        result = new DummyRecognitionResult();
        result.setUtterance("test utterance");
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent#getEventType()}.
     */
    @Test
    public void testGetEventType() {
        final RecognitionEvent event = new RecognitionEvent(result);
        Assert.assertEquals(RecognitionEvent.EVENT_TYPE, event.getEventType());
        Assert.assertEquals(RecognitionEvent.class.getName(),
                event.getEventType());
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent#getInputResult()}.
     */
    @Test
    public void testGetInputResult() {
        final RecognitionEvent event = new RecognitionEvent(result);
        final RecognitionResult currentResult =
            (RecognitionResult) event.getInputResult();
        Assert.assertEquals("test utterance", currentResult.getUtterance());
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent#getRecognitionResult()}.
     */
    @Test
    public void testGetRecognitionResult() {
        final RecognitionEvent event = new RecognitionEvent(result);
        Assert.assertEquals(result, event.getRecognitionResult());
    }
}
