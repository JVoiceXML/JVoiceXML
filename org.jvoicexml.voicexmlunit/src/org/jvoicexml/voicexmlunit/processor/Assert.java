/**
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
                conversation.countStatements());
    }
}
