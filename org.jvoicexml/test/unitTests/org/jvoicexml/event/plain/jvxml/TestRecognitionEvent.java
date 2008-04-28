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

import junit.framework.TestCase;

import org.jvoicexml.test.DummyRecognitionResult;

/**
 * Test case for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestRecognitionEvent
        extends TestCase {
    /** A test result. */
    private DummyRecognitionResult result;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        result = new DummyRecognitionResult();
        result.setUtterance("test utterance");
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent#getEventType()}.
     */
    public void testGetEventType() {
        final RecognitionEvent event = new RecognitionEvent(result);
        assertEquals(RecognitionEvent.EVENT_TYPE, event.getEventType());
        assertEquals(RecognitionEvent.class.getName(), event.getEventType());
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent#getInputResult()}.
     */
    public void testGetInputResult() {
        final RecognitionEvent event = new RecognitionEvent(result);
        assertEquals("'test utterance'", event.getInputResult());
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent#getRecognitionResult()}.
     */
    public void testGetRecognitionResult() {
        final RecognitionEvent event = new RecognitionEvent(result);
        assertEquals(result, event.getRecognitionResult());
    }
}
