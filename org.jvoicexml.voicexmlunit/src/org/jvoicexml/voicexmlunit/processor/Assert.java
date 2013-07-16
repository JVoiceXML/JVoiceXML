/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/Mmi.java $
 * Version: $LastChangedRevision: 3651 $
 * Date:    $Date: 2013-02-27 00:16:33 +0100 (Wed, 27 Feb 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.processor;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.io.Hangup;
import org.jvoicexml.voicexmlunit.io.Recording;
import org.jvoicexml.voicexmlunit.io.Assertion;

import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * @author thesis
 * Extension to Assert a statement subclass object.
 */
public final class Assert extends org.junit.Assert implements Facade {

    /** Current assertion under processing.  */
    private Assertion assertion;

    /** Call object.  */
    private Call call;

    /**
     * Set current assertion to process.
     * @param assertion the assertion to use, set to null if none
     * @param call the Call object, null if you want a mock
     */
    public Assert(final Assertion assertion, Call call) {
        this.assertion = assertion;
        this.call = call;
    }

    /**
     * Set current assertion to process.
     * @param assertion the assertion to use, set to null if none
     */
    public Assert(final Assertion assertion) {
        this(assertion, null);
    }

    /**
     * Assert an Output instance with the given message.
     * @param message  Message to expect in the call
     */
    @Override
    public void assertOutput(final SsmlDocument message) {
        assertNotNull(assertion);
        assertion.receive(message);
    }

    /**
     * Assert that the statement is an Input instance and the actual message
     * can be send with the given call.
     */
    @Override
    public void assertInput() {
        assertNotNull(assertion);
        Recording record;
        if (call == null) {
            record = new Recording(null, null); // mock
        } else {
            record = call.record();
        }
        assertion.send(record);
    }

    /**
     * Asserts the final hangup.
     */
    @Override
    public void assertHangup() {
        try {
            Hangup hangup = new Hangup(assertion);
            final SsmlDocument message = hangup.toSsml();
            hangup.receive(message); // fails always
        } catch (ParserConfigurationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Assert the expected count of conversation statements.
     *
     * @param expectedCount How many statements should we have?
     * @param conversation
     */
    public static void assertStatements(final int expectedCount,
            final Conversation conversation) {
        assertEquals("statements", expectedCount,
                conversation.size());
    }
}
