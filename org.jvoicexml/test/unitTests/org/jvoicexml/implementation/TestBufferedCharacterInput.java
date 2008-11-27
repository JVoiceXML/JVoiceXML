/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Test case for the {@link BufferedCharacterInput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestBufferedCharacterInput implements SpokenInputListener {
    /** The test object. */
    private BufferedCharacterInput input;

    /** Synchronisation. */
    private final Object lock = new Object();

    /** The last received utterance. */
    private String utterance;

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         Test failed.
     */
    @Before
    public void setUp() throws Exception {
        input = new BufferedCharacterInput();
        input.addListener(this);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.BufferedCharacterInput#startRecognition()}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testStartRecognition() throws JVoiceXMLEvent, Exception {
        input.startRecognition();
        final char dtmf = '4';
        input.addCharacter(dtmf);
        synchronized (lock) {
            lock.wait();
        }
        input.stopRecognition();
        Assert.assertEquals(Character.toString(dtmf), utterance);
    }

    /**
     * {@inheritDoc}
     */
    public void inputStatusChanged(final SpokenInputEvent event) {
        final RecognitionResult result = (RecognitionResult) event.getParam();
        utterance = result.getUtterance();
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
